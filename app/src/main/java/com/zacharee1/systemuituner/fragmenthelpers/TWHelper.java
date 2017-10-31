package com.zacharee1.systemuituner.fragmenthelpers;

import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.misc.SettingsUtils;
import com.zacharee1.systemuituner.prefs.SliderPreferenceEmbedded;

public class TWHelper extends BaseHelper
{
    private final ItemDetailFragment mFragment;

    public TWHelper(ItemDetailFragment fragment) {
        mFragment = fragment;

//        setHBWState();
//        setHBWListener();
        setUpQSStuff();
    }

    private void setUpQSStuff() {
        SliderPreferenceEmbedded rows = (SliderPreferenceEmbedded) mFragment.findPreference("qs_tile_row");
        SliderPreferenceEmbedded columns = (SliderPreferenceEmbedded) mFragment.findPreference("qs_tile_column");
        int defVal = 3;
        int savedRowVal = Settings.Secure.getInt(mFragment.getActivity().getContentResolver(), rows.getKey(), defVal);
        int savedColVal = Settings.Secure.getInt(mFragment.getActivity().getContentResolver(), columns.getKey(), defVal);

        rows.setProgressState(savedRowVal);
        columns.setProgressState(savedColVal);

        rows.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsUtils.writeSecure(mFragment.getActivity(), preference.getKey(), newValue.toString());
                return true;
            }
        });
        columns.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsUtils.writeSecure(mFragment.getActivity(), preference.getKey(), newValue.toString());
                return true;
            }
        });
    }

//    private void setHBWState() {
//        String hbw = Settings.Global.getString(mFragment.getContext().getContentResolver(), "limit_brightness_state");
//        SwitchPreference hbwSwitch = (SwitchPreference) mFragment.findPreference("high_bright_warning");
//
//        if (hbw == null || hbw.isEmpty()) {
//            hbwSwitch.setChecked(false);
//        } else {
//            hbwSwitch.setChecked(true);
//        }
//
//        hbwSwitch.setChecked(false);
//        hbwSwitch.setEnabled(false);
//    }

//    private void setHBWListener() {
//        SwitchPreference hbwSwitch = (SwitchPreference) mFragment.findPreference("high_bright_warning");
//
//        hbwSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
//        {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o)
//            {
//                if (Boolean.valueOf(o.toString())) {
//                    SettingsUtils.writeGlobal(mFragment.getContext(), "limit_brightness_state", "80,80");
//                } else {
//                    SettingsUtils.writeGlobal(mFragment.getContext(), "limit_brightness_state", "");
//                }
//                return true;
//            }
//        });
//    }

    @Override
    public void onDestroy() {

    }
}
