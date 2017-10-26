package com.zacharee1.systemuituner.fragmenthelpers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.zacharee1.systemuituner.activites.SetupActivity;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.DemoHandler;
import com.zacharee1.systemuituner.prefs.SliderPreference;
import com.zacharee1.systemuituner.prefs.SliderPreferenceEmbedded;
import com.zacharee1.systemuituner.prefs.TimePreference;
import com.zacharee1.systemuituner.misc.SettingsUtils;

import java.util.List;

public class DemoHelper extends BaseHelper
{
    private final ItemDetailFragment mFragment;
    private DemoHandler mDemoHandler;
    private BroadcastReceiver switchReceiver;

    public DemoHelper(ItemDetailFragment fragment) {
        mFragment = fragment;
        mDemoHandler = new DemoHandler(mFragment.getContext());

        if (SettingsUtils.hasSpecificPerm(mFragment.getContext(), Manifest.permission.DUMP)) {
            setPrefListeners();
            setDemoSwitchListener();
        } else {
            Intent intent = new Intent(mFragment.getContext(), SetupActivity.class);
            intent.putExtra("permission_needed", new String[] { Manifest.permission.DUMP });
            mFragment.getActivity().startActivity(intent);

            mFragment.getActivity().finish();
        }
    }

    private void setPrefListeners() {
        final Preference enableDemo = mFragment.findPreference("sysui_demo_allowed");
        enableDemo.setEnabled(Settings.Global.getInt(mFragment.getContext().getContentResolver(), "sysui_demo_allowed", 0) == 0);
        enableDemo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                if (mFragment.getActivity().checkCallingOrSelfPermission("android.permission.DUMP") == PackageManager.PERMISSION_GRANTED) {
                    SettingsUtils.writeGlobal(mFragment.getContext(), preference.getKey(), "1");
                    mFragment.findPreference("show_demo").setEnabled(true);
                } else {
                    Toast.makeText(mFragment.getContext(), mFragment.getResources().getString(R.string.grant_dump_perm), Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    private void setDemoSwitchListener() {
        final SwitchPreference demo = (SwitchPreference) mFragment.findPreference("show_demo");

        switchReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean enabled = mDemoHandler.isEnabled();

                demo.setChecked(enabled);
                disableOtherPreferences(enabled);
            }
        };

        IntentFilter filter = new IntentFilter("com.android.systemui.demo");

        mFragment.getActivity().registerReceiver(switchReceiver, filter);

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
        for (int i = 0; i < mFragment.getPreferenceScreen().getRootAdapter().getCount(); i++) {
            Object item = mFragment.getPreferenceScreen().getRootAdapter().getItem(i);

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
            mFragment.getActivity().unregisterReceiver(switchReceiver);
        } catch (Exception e) {}
    }
}
