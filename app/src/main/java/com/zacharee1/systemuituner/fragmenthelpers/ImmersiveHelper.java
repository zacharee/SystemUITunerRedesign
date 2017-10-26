package com.zacharee1.systemuituner.fragmenthelpers;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.provider.Settings;
import android.util.Log;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.misc.ImmersiveHandler;

public class ImmersiveHelper extends BaseHelper implements Preference.OnPreferenceChangeListener {
    private ItemDetailFragment mFragment;

    private CheckBoxPreference none;
    private CheckBoxPreference full;
    private CheckBoxPreference status;
    private CheckBoxPreference navi;
    private CheckBoxPreference preconf;
    private ContentObserver mObserver;

    public ImmersiveHelper(ItemDetailFragment fragment) {
        mFragment = fragment;
        none = (CheckBoxPreference) mFragment.findPreference(ImmersiveHandler.DISABLED);
        full = (CheckBoxPreference) mFragment.findPreference(ImmersiveHandler.FULL);
        status = (CheckBoxPreference) mFragment.findPreference(ImmersiveHandler.STATUS);
        navi = (CheckBoxPreference) mFragment.findPreference(ImmersiveHandler.NAV);
        preconf = (CheckBoxPreference) mFragment.findPreference(ImmersiveHandler.PRECONF);

        mFragment.findPreference("immersive_tile_mode").setOnPreferenceChangeListener(this);

        none.setOnPreferenceChangeListener(this);
        full.setOnPreferenceChangeListener(this);
        status.setOnPreferenceChangeListener(this);
        navi.setOnPreferenceChangeListener(this);
        preconf.setOnPreferenceChangeListener(this);

        setContentObserver();
        setProperBoxChecked();
        disableQSSettingIfBelowNougat();
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

        mFragment.getActivity().getContentResolver().registerContentObserver(Settings.Global.CONTENT_URI, true, mObserver);
    }

    private void setProperBoxChecked() {
        String currentMode = ImmersiveHandler.getMode(mFragment.getContext(), false);

        if (currentMode == null || currentMode.isEmpty()) {
            currentMode = ImmersiveHandler.DISABLED;
        }

        setAllOthersDisabled(currentMode);
    }

    private void setAllOthersDisabled(String keyToNotDisable) {
        none.setChecked(none.getKey().equals(keyToNotDisable));
        full.setChecked(full.getKey().equals(keyToNotDisable));
        status.setChecked(status.getKey().equals(keyToNotDisable));
        navi.setChecked(navi.getKey().equals(keyToNotDisable));
        preconf.setChecked(preconf.getKey().equals(keyToNotDisable));
    }

    private void disableQSSettingIfBelowNougat() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            PreferenceCategory category = (PreferenceCategory) mFragment.findPreference("config_qs");

            for (int i = 0; i < category.getPreferenceCount(); i++) {
                Preference preference = category.getPreference(i);
                preference.setEnabled(false);
                preference.setSummary(R.string.requires_nougat);
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            mFragment.getActivity().getContentResolver().unregisterContentObserver(mObserver);
        } catch (Exception e) {}
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference instanceof CheckBoxPreference) {
            CheckBoxPreference boxPreference = (CheckBoxPreference) preference;

            boolean isChecked = Boolean.valueOf(o.toString());

            if (!isChecked) boxPreference.setChecked(true);
            else {
                setAllOthersDisabled(boxPreference.getKey());
                ImmersiveHandler.setMode(mFragment.getActivity(), boxPreference.getKey());
            }
        }

        if (preference instanceof ListPreference) {
            String which = o.toString();

            if (ImmersiveHandler.isInImmersive(mFragment.getContext())) ImmersiveHandler.setMode(mFragment.getContext(), which);
        }

        return true;
    }
}
