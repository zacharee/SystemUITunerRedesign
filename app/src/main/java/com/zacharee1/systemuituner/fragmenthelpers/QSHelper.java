package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.ItemDetailFragment;
import com.zacharee1.systemuituner.SliderPreference;
import com.zacharee1.systemuituner.utils.SettingsUtils;

public class QSHelper
{
    private ItemDetailFragment mFragment;
    private final SharedPreferences mSharedPreferences;

    public QSHelper(ItemDetailFragment fragment) {
        mFragment = fragment;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mFragment.getContext());

        setIconTints();
        setSwitchStates();
        setSwitchListeners();
        setSliderState();
    }

    private void setIconTints() {
        for (int i = 0; i < mFragment.getPreferenceScreen().getRootAdapter().getCount(); i++) {
            Object pref = mFragment.getPreferenceScreen().getRootAdapter().getItem(i);

            if (pref instanceof Preference)
            {
                Preference preference = (Preference) pref;
                Drawable icon = preference.getIcon();

                if (icon != null)
                {
                    boolean DARK = mSharedPreferences.getBoolean("dark_mode", false);
                    if (DARK)
                    {
                        icon.setTintList(ColorStateList.valueOf(Color.WHITE));
                    }
                }
            }
        }
    }

    private void setSwitchStates() {
        for (int i = 0; i < mFragment.getPreferenceScreen().getRootAdapter().getCount(); i++) { //loop through every preference
            Object o = mFragment.getPreferenceScreen().getRootAdapter().getItem(i);

            if (o instanceof SwitchPreference) { //if current preference is a SwitchPreference
                SwitchPreference pref = (SwitchPreference) o;

                pref.setChecked(Settings.Secure.getInt(mFragment.getContext().getContentResolver(), pref.getKey(), 1) == 1);
            }
        }
    }

    private void setSwitchListeners() {
        for (int i = 0; i < mFragment.getPreferenceScreen().getRootAdapter().getCount(); i++) { //loop through every preference
            Object o = mFragment.getPreferenceScreen().getRootAdapter().getItem(i);

            if (o instanceof SwitchPreference) { //if current preference is a SwitchPreference
                final SwitchPreference pref = (SwitchPreference) o;

                pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o)
                    {
                        if (Boolean.valueOf(o.toString())) {
                            SettingsUtils.writeSecure(mFragment.getContext(), preference.getKey(), "1");
                        } else {
                            SettingsUtils.writeSecure(mFragment.getContext(), preference.getKey(), "0");
                        }
                        return true;
                    }
                });
            }
        }
    }

    private void setSliderState() {
        SliderPreference preference = (SliderPreference) mFragment.findPreference("sysui_qqs_count"); //find the SliderPreference

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                final SliderPreference pref = (SliderPreference) preference;
                pref.setMaxProgess(20);
                pref.setProgressState(Settings.Secure.getInt(mFragment.getContext().getContentResolver(), "sysui_qqs_count", 5)); //set the progress/value from Settings
                pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o)
                    {
                        SettingsUtils.writeSecure(mFragment.getContext(), "sysui_qqs_count", o.toString()); //write new value to Settings if user presses OK
                        return true;
                    }
                });
                return true;
            }
        });

    }
}
