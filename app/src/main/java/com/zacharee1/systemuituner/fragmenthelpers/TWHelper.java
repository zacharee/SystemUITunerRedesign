package com.zacharee1.systemuituner.fragmenthelpers;

import android.preference.Preference;
import android.provider.Settings;

import com.zacharee1.sliderpreferenceembedded.SliderPreferenceEmbeddedNew;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.misc.SettingsUtils;

public class TWHelper extends BaseHelper
{

    public TWHelper(ItemDetailFragment fragment) {
        super(fragment);
//        setHBWState();
//        setHBWListener();
        setUpQSStuff();
    }

    private void setUpQSStuff() {
        final SliderPreferenceEmbeddedNew rows = (SliderPreferenceEmbeddedNew) findPreference("qs_tile_row");
        final SliderPreferenceEmbeddedNew columns = (SliderPreferenceEmbeddedNew) findPreference("qs_tile_column");
        int defVal = 3;
        final int savedRowVal = Settings.Secure.getInt(getActivity().getContentResolver(), rows.getKey(), defVal);
        final int savedColVal = Settings.Secure.getInt(getActivity().getContentResolver(), columns.getKey(), defVal);

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
//            rows.setMinProgress(1);
//            columns.setMinProgress(1);
//        }

//        rows.setMinProgress(1);
//        columns.setMax(2);

        rows.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsUtils.writeSecure(getActivity(), preference.getKey(), newValue.toString());
                return true;
            }
        });
        columns.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsUtils.writeSecure(getActivity(), preference.getKey(), newValue.toString());
                return true;
            }
        });

        rows.setProgress(savedRowVal);
        columns.setProgress(savedColVal);
    }

//    private void setHBWState() {
//        String hbw = Settings.Global.getString(getContext().getContentResolver(), "limit_brightness_state");
//        SwitchPreference hbwSwitch = (SwitchPreference) findPreference("high_bright_warning");
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
//        SwitchPreference hbwSwitch = (SwitchPreference) findPreference("high_bright_warning");
//
//        hbwSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
//        {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object o)
//            {
//                if (Boolean.valueOf(o.toString())) {
//                    SettingsUtils.writeGlobal(getContext(), "limit_brightness_state", "80,80");
//                } else {
//                    SettingsUtils.writeGlobal(getContext(), "limit_brightness_state", "");
//                }
//                return true;
//            }
//        });
//    }

    @Override
    public void onDestroy() {

    }
}
