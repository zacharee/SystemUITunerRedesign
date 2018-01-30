package com.zacharee1.systemuituner.fragmenthelpers;

import android.graphics.Color;
import android.preference.Preference;
import android.provider.Settings;

import com.jaredrummler.android.colorpicker.ColorPreference;
import com.zacharee1.sliderpreferenceembedded.SliderPreferenceEmbedded;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.util.SettingsUtils;

public class TWHelper extends BaseHelper
{
    public static final String TILE_ROW = "qs_tile_row";
    public static final String TILE_COLUMN = "qs_tile_column";
    public static final String NAVBAR_COLOR = "navigationbar_color";
    public static final String NAVBAR_CURRENT_COLOR = "navigationbar_current_color";

    private StatbarHelper statbarHelper;

    public TWHelper(ItemDetailFragment fragment) {
        super(fragment);
        statbarHelper = new StatbarHelper(fragment);
        setUpQSStuff();
        setUpNavBarStuff();
    }

    private void setUpQSStuff() {
        final SliderPreferenceEmbedded rows = (SliderPreferenceEmbedded) findPreference(TILE_ROW);
        final SliderPreferenceEmbedded columns = (SliderPreferenceEmbedded) findPreference(TILE_COLUMN);
        int defVal = 3;
        final int savedRowVal = Settings.Secure.getInt(getActivity().getContentResolver(), rows.getKey(), defVal);
        final int savedColVal = Settings.Secure.getInt(getActivity().getContentResolver(), columns.getKey(), defVal);

        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getSharedPreferences().edit().putInt(preference.getKey(), Integer.valueOf(newValue.toString())).apply();
                SettingsUtils.writeSecure(getActivity(), preference.getKey(), newValue.toString());
                return true;
            }
        };

        rows.setOnPreferenceChangeListener(listener);
        columns.setOnPreferenceChangeListener(listener);

        rows.setProgress(savedRowVal);
        columns.setProgress(savedColVal);
    }

    private void setUpNavBarStuff() {
        final ColorPreference preference = (ColorPreference) findPreference(NAVBAR_COLOR);
        int savedVal = Settings.Global.getInt(getContext().getContentResolver(), NAVBAR_COLOR, Color.WHITE);

        preference.saveValue(savedVal);
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsUtils.writeGlobal(getContext(), NAVBAR_COLOR, newValue.toString());
                SettingsUtils.writeGlobal(getContext(), NAVBAR_CURRENT_COLOR, newValue.toString());
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        statbarHelper.onDestroy();
    }
}
