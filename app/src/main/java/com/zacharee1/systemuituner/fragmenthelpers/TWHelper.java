package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.ItemDetailFragment;
import com.zacharee1.systemuituner.utils.SettingsUtils;

public class TWHelper
{
    private ItemDetailFragment mFragment;
    private final SharedPreferences mSharedPreferences;

    public TWHelper(ItemDetailFragment fragment) {
        mFragment = fragment;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mFragment.getContext());

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
