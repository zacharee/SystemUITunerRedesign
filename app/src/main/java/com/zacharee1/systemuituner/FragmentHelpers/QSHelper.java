package com.zacharee1.systemuituner.FragmentHelpers;

import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.zacharee1.systemuituner.ItemDetailFragment;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.SliderPreference;
import com.zacharee1.systemuituner.Utils.SettingsUtils;

public class QSHelper
{
    private ItemDetailFragment mFragment;

    public QSHelper(ItemDetailFragment fragment) {
        mFragment = fragment;
        setSwitchStates();
        setSwitchListeners();
        setSliderState();
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
                pref.setProgressState(Settings.Secure.getInt(mFragment.getContext().getContentResolver(), "sysui_qqs_count", 5)); //set the progress/value from Settings
                pref.setOnDialogClosedListener(new SliderPreference.OnDialogClosedListener()
                {
                    @Override
                    public void onDialogClosed(boolean positiveResult, int progress)
                    {
                        if (positiveResult) {
                            SettingsUtils.writeSecure(mFragment.getContext(), "sysui_qqs_count", progress + ""); //write new value to Settings if user presses OK
                        }
                    }
                });
                return true;
            }
        });

    }
}
