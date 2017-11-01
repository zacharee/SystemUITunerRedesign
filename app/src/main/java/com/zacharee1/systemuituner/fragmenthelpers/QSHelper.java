package com.zacharee1.systemuituner.fragmenthelpers;

import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.prefs.SliderPreference;
import com.zacharee1.systemuituner.misc.SettingsUtils;
import com.zacharee1.systemuituner.prefs.SliderPreferenceEmbedded;

public class QSHelper extends BaseHelper
{
    public QSHelper(ItemDetailFragment fragment) {
        super(fragment);
        
        setSwitchStates();
        setSwitchListeners();
        setSliderState();
    }

    private void setSwitchStates() {
        for (int i = 0; i < getPreferenceScreen().getRootAdapter().getCount(); i++) { //loop through every preference
            Object o = getPreferenceScreen().getRootAdapter().getItem(i);

            if (o instanceof SwitchPreference) { //if current preference is a SwitchPreference
                SwitchPreference pref = (SwitchPreference) o;

                pref.setChecked(Settings.Secure.getInt(getContext().getContentResolver(), pref.getKey(), 1) == 1);
            }
        }
    }

    private void setSwitchListeners() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            for (int i = 0; i < getPreferenceScreen().getRootAdapter().getCount(); i++)
            { //loop through every preference
                Object o = getPreferenceScreen().getRootAdapter().getItem(i);

                if (o instanceof SwitchPreference)
                { //if current preference is a SwitchPreference
                    final SwitchPreference pref = (SwitchPreference) o;

                    pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                    {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object o)
                        {
                            if (Boolean.valueOf(o.toString()))
                            {
                                SettingsUtils.writeSecure(getContext(), preference.getKey(), "1");
                            } else
                            {
                                SettingsUtils.writeSecure(getContext(), preference.getKey(), "0");
                            }
                            return true;
                        }
                    });
                }
            }
        } else {
            PreferenceCategory category = (PreferenceCategory) findPreference("general_qs");
            category.setEnabled(false);

            for (int i = 0; i < category.getPreferenceCount(); i++) {
                SwitchPreference preference = (SwitchPreference) category.getPreference(i);
                preference.setChecked(false);
                preference.setSummary(R.string.requires_nougat);
            }
        }
    }

    private void setSliderState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {

            SliderPreferenceEmbedded pref = (SliderPreferenceEmbedded) findPreference("sysui_qqs_count"); //find the SliderPreference

            pref.setMaxProgess(20);
            pref.setMinProgress(1);
            pref.setProgressState(Settings.Secure.getInt(getContext().getContentResolver(), "sysui_qqs_count", 5)); //set the progress/value from Settings
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.writeSecure(getContext(), "sysui_qqs_count", o.toString()); //write new value to Settings if user presses OK
                    return true;
                }
            });
        } else {
            PreferenceCategory category = (PreferenceCategory) findPreference("qqs_count_category");
            category.setEnabled(false);

            for (int i = 0; i < category.getPreferenceCount(); i++) {
                Preference preference = category.getPreference(i);
                preference.setSummary(R.string.requires_nougat);
            }
        }

    }

    @Override
    public void onDestroy() {

    }
}
