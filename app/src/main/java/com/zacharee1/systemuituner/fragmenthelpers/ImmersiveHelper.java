package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.activites.apppickers.ImmersiveSelectActivity;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.handlers.ImmersiveHandler;

public class ImmersiveHelper extends BaseHelper implements Preference.OnPreferenceChangeListener {
    public static final String IMMERSIVE_BOXES = "imm_boxes";
    public static final String APP_SPECIFIC = "app_specific";
    public static final String APP_IMMERSIVE = "app_immsersive";
    public static final String IMMERSIVE_BLACKLIST = "immersive_blacklist";
    public static final String SELECT_APPS = "select_apps";
    public static final String CONFIG_QS = "config_qs";
    public static final String IMMERSIVE_TILE_MODE = "immersive_tile_mode";

    private CheckBoxPreference none;
    private CheckBoxPreference full;
    private CheckBoxPreference status;
    private CheckBoxPreference navi;
    private CheckBoxPreference preconf;
    private ContentObserver mObserver;

    public ImmersiveHelper(ItemDetailFragment fragment) {
        super(fragment);

        none = (CheckBoxPreference) findPreference(ImmersiveHandler.DISABLED);
        full = (CheckBoxPreference) findPreference(ImmersiveHandler.FULL);
        status = (CheckBoxPreference) findPreference(ImmersiveHandler.STATUS);
        navi = (CheckBoxPreference) findPreference(ImmersiveHandler.NAV);
        preconf = (CheckBoxPreference) findPreference(ImmersiveHandler.PRECONF);

        findPreference("immersive_tile_mode").setOnPreferenceChangeListener(this);

        none.setOnPreferenceChangeListener(this);
        full.setOnPreferenceChangeListener(this);
        status.setOnPreferenceChangeListener(this);
        navi.setOnPreferenceChangeListener(this);
        preconf.setOnPreferenceChangeListener(this);

        setContentObserver();
        setProperBoxChecked();
        disableQSSettingIfBelowNougat();
        setSelectorListener();
    }

    private void setContentObserver() {
        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                if (uri.equals(ImmersiveHandler.POLICY_CONTROL)) {
                    setProperBoxChecked();
                }
            }
        };

        getActivity().getContentResolver().registerContentObserver(Settings.Global.CONTENT_URI, true, mObserver);
    }

    private void setProperBoxChecked() {
        String currentMode = ImmersiveHandler.getMode(getContext());

        if (currentMode == null || currentMode.isEmpty()) {
            currentMode = ImmersiveHandler.DISABLED;
        }

        setAllOthersDisabled(currentMode);
    }

    private void setAllOthersDisabled(String keyToNotDisable) {
        PreferenceCategory boxes = (PreferenceCategory) findPreference(IMMERSIVE_BOXES);

        for (int i = 0; i < boxes.getPreferenceCount(); i++) {
            Preference preference = boxes.getPreference(i);

            if (preference instanceof CheckBoxPreference) {
                CheckBoxPreference p = (CheckBoxPreference) preference;
                p.setEnabled(!p.getKey().equals(keyToNotDisable));

                if (!p.getKey().equals(keyToNotDisable)) {
                    p.setChecked(false);
                } else if (!p.isChecked()) {
                    p.setChecked(true);
                }
            }
        }
    }

    private void disableQSSettingIfBelowNougat() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            PreferenceCategory category = (PreferenceCategory) findPreference(CONFIG_QS);

            for (int i = 0; i < category.getPreferenceCount(); i++) {
                Preference preference = category.getPreference(i);
                preference.setEnabled(false);
                preference.setSummary(R.string.requires_nougat);
            }
        }
    }

    private void setSelectorListener() {
        Preference preference = findPreference(SELECT_APPS);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().startActivity(new Intent(getActivity(), ImmersiveSelectActivity.class));
                return true;
            }
        });

        SwitchPreference enabled = (SwitchPreference) findPreference(APP_IMMERSIVE);
        enabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ImmersiveHandler.setMode(getContext(), ImmersiveHandler.getMode(getContext()));
                    }
                }, 100);
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        try {
            getActivity().getContentResolver().unregisterContentObserver(mObserver);
        } catch (Exception e) {}
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference instanceof CheckBoxPreference) {
            CheckBoxPreference boxPreference = (CheckBoxPreference) preference;

            boolean isChecked = Boolean.valueOf(o.toString());

            if (isChecked) {
                setAllOthersDisabled(boxPreference.getKey());
                ImmersiveHandler.setMode(getActivity(), boxPreference.getKey());
            }
        }

        if (preference instanceof ListPreference) {
            String which = o.toString();

            if (ImmersiveHandler.isInImmersive(getContext())) ImmersiveHandler.setMode(getContext(), which);
        }

        return true;
    }
}
