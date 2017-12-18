package com.zacharee1.systemuituner.fragmenthelpers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.Toast;

import com.zacharee1.systemuituner.activites.instructions.SetupActivity;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.handlers.DemoHandler;
import com.zacharee1.systemuituner.util.SettingsUtils;

public class DemoHelper extends BaseHelper
{
    public static final String PERMISSION_NEEDED = "permission_needed";
    public static final String DEMO_ALLOWED = "sysui_demo_allowed";
    public static final String SHOW_DEMO = "show_demo";

    public static final String DEMO_ACTION = "com.android.systemui.demo";

    private DemoHandler mDemoHandler;
    private BroadcastReceiver switchReceiver;

    public DemoHelper(ItemDetailFragment fragment) {
        super(fragment);
        mDemoHandler = new DemoHandler(getContext());

        if (SettingsUtils.hasSpecificPerm(getContext(), Manifest.permission.DUMP)) {
            setPrefListeners();
            setDemoSwitchListener();
        } else {
            Intent intent = new Intent(getContext(), SetupActivity.class);
            intent.putExtra(PERMISSION_NEEDED, new String[] { Manifest.permission.DUMP });
            startActivity(intent);

            getActivity().finish();
        }
    }

    private void setPrefListeners() {
        final Preference enableDemo = findPreference(DEMO_ALLOWED);
        enableDemo.setEnabled(Settings.Global.getInt(getContext().getContentResolver(), DEMO_ALLOWED, 0) == 0);
        enableDemo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                if (getActivity().checkCallingOrSelfPermission(Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED) {
                    SettingsUtils.writeGlobal(getContext(), preference.getKey(), "1");
                    findPreference(SHOW_DEMO).setEnabled(true);
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.grant_dump_perm), Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    private void setDemoSwitchListener() {
        final SwitchPreference demo = (SwitchPreference) findPreference(SHOW_DEMO);

        switchReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean enabled = mDemoHandler.isEnabled();

                demo.setChecked(enabled);
                disableOtherPreferences(enabled);
            }
        };

        IntentFilter filter = new IntentFilter(DEMO_ACTION);

        getActivity().registerReceiver(switchReceiver, filter);

        demo.setEnabled(mDemoHandler.isAllowed());

        demo.setChecked(mDemoHandler.isEnabled());

        disableOtherPreferences(mDemoHandler.isEnabled());

        demo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                if (Boolean.valueOf(o.toString())) {
                    showDemo();
                } else {
                    hideDemo();
                }

                return true;
            }
        });
    }

    private void showDemo() {
        disableOtherPreferences(true);
        mDemoHandler.showDemo();
    }

    private void hideDemo() {
        disableOtherPreferences(false);
        mDemoHandler.hideDemo();
    }

    private void disableOtherPreferences(boolean disable) {
        for (int i = 0; i < getPreferenceScreen().getRootAdapter().getCount(); i++) {
            Object item = getPreferenceScreen().getRootAdapter().getItem(i);

            if (item instanceof Preference) {
                Preference preference = (Preference) item;

                if (preference.hasKey() && !(preference.getKey().equals(DEMO_ALLOWED) || preference.getKey().equals(SHOW_DEMO))) {
                    preference.setEnabled(!disable);
                }
            }
        }
    }

    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(switchReceiver);
        } catch (Exception e) {}
    }
}
