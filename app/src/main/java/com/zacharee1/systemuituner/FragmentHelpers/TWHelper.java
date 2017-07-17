package com.zacharee1.systemuituner.FragmentHelpers;

import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.ItemDetailFragment;
import com.zacharee1.systemuituner.Utils.SettingsUtils;

/**
 * Created by Zacha on 7/16/2017.
 */

public class TWHelper
{
    private ItemDetailFragment mFragment;

    public TWHelper(ItemDetailFragment fragment) {
        mFragment = fragment;

        setHBWState();
        setHBWListener();
    }

    private void setHBWState() {
        String hbw = Settings.Global.getString(mFragment.getContext().getContentResolver(), "limit_brightness_state");
        SwitchPreference hbwSwitch = (SwitchPreference) mFragment.findPreference("high_bright_warning");

        if (hbw == null || hbw.isEmpty()) {
            hbwSwitch.setChecked(false);
        } else {
            hbwSwitch.setChecked(true);
        }
    }

    private void setHBWListener() {
        SwitchPreference hbwSwitch = (SwitchPreference) mFragment.findPreference("high_bright_warning");

        hbwSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                if (Boolean.valueOf(o.toString())) {
                    SettingsUtils.writeGlobal(mFragment.getContext(), "limit_brightness_state", "80,80");
                } else {
                    SettingsUtils.writeGlobal(mFragment.getContext(), "limit_brightness_state", "");
                }
                return true;
            }
        });
    }
}
