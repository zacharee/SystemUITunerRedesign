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

import com.zacharee1.systemuituner.activites.SetupActivity;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.DemoHandler;
import com.zacharee1.systemuituner.misc.SettingsUtils;

public class DemoHelper extends BaseHelper
{
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
            intent.putExtra("permission_needed", new String[] { Manifest.permission.DUMP });
            getActivity().startActivity(intent);

            getActivity().finish();
        }
    }

    private void setPrefListeners() {
        final Preference enableDemo = findPreference("sysui_demo_allowed");
        enableDemo.setEnabled(Settings.Global.getInt(getContext().getContentResolver(), "sysui_demo_allowed", 0) == 0);
        enableDemo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                if (getActivity().checkCallingOrSelfPermission("android.permission.DUMP") == PackageManager.PERMISSION_GRANTED) {
                    SettingsUtils.writeGlobal(getContext(), preference.getKey(), "1");
                    findPreference("show_demo").setEnabled(true);
                } else {
                    Toast.makeText(getContext(), getActivity().getResources().getString(R.string.grant_dump_perm), Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    private void setDemoSwitchListener() {
        final SwitchPreference demo = (SwitchPreference) findPreference("show_demo");

        switchReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean enabled = mDemoHandler.isEnabled();

                demo.setChecked(enabled);
                disableOtherPreferences(enabled);
            }
        };

        IntentFilter filter = new IntentFilter("com.android.systemui.demo");

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

                if (preference.hasKey() && !(preference.getKey().equals("sysui_demo_allowed") || preference.getKey().equals("show_demo"))) {
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
