package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.Intent;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.sliderpreferenceembedded.SliderPreferenceEmbedded;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.activites.QuickSettingsLayoutEditor;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.util.SettingsUtils;

public class QSHelper extends BaseHelper
{
    public static final String GENERAL_QS = "general_qs";
    public static final String QQS_COUNT = "sysui_qqs_count";
    public static final String COUNT_CATEGORY = "qqs_count_category";

    public QSHelper(ItemDetailFragment fragment) {
        super(fragment);
        
        setSwitchStates();
        setSwitchListeners();
        setSliderState();
        setEditorListener();
    }

    private void setEditorListener() {
        Preference launch = findPreference("launch_editor");
        launch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(new Intent(getContext(), QuickSettingsLayoutEditor.class));
                return true;
            }
        });
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
            PreferenceCategory category = (PreferenceCategory) findPreference(GENERAL_QS);
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

            final SliderPreferenceEmbedded pref = (SliderPreferenceEmbedded) findPreference(QQS_COUNT); //find the SliderPreference
//            pref.set<in(1);
            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.writeSecure(getContext(), QQS_COUNT, o.toString()); //write new value to Settings if user presses OK
                    return true;
                }
            });

            pref.setProgress(Settings.Secure.getInt(getContext().getContentResolver(), QQS_COUNT, 5)); //set the progress/value from Settings
        } else {
            PreferenceCategory category = (PreferenceCategory) findPreference(COUNT_CATEGORY);
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
