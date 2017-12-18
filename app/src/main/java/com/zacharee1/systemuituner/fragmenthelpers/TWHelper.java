package com.zacharee1.systemuituner.fragmenthelpers;

import android.preference.Preference;
import android.provider.Settings;

import com.zacharee1.sliderpreferenceembedded.SliderPreferenceEmbedded;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.util.SettingsUtils;

public class TWHelper extends BaseHelper
{
    public static final String TILE_ROW = "qs_tile_row";
    public static final String TILE_COLUMN = "qs_tile_column";

    public TWHelper(ItemDetailFragment fragment) {
        super(fragment);
        setUpQSStuff();
    }

    private void setUpQSStuff() {
        final SliderPreferenceEmbedded rows = (SliderPreferenceEmbedded) findPreference(TILE_ROW);
        final SliderPreferenceEmbedded columns = (SliderPreferenceEmbedded) findPreference(TILE_COLUMN);
        int defVal = 3;
        final int savedRowVal = Settings.Secure.getInt(getActivity().getContentResolver(), rows.getKey(), defVal);
        final int savedColVal = Settings.Secure.getInt(getActivity().getContentResolver(), columns.getKey(), defVal);

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

    @Override
    public void onDestroy() {

    }
}
