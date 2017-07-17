package com.zacharee1.systemuituner.FragmentHelpers;

import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.zacharee1.systemuituner.ItemDetailFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class StatbarHelper
{
    private ItemDetailFragment mFragment;

    public StatbarHelper(ItemDetailFragment fragment) {
        mFragment = fragment;

        preferenceListeners();
        setSwitchPreferenceStates();
        switchPreferenceListeners();
    }

    private void preferenceListeners() {
        Preference resetBL = mFragment.findPreference("reset_blacklist");
        Preference backupBL = mFragment.findPreference("backup_blacklist");
        Preference restoreBL = mFragment.findPreference("restore_blacklist");

        resetBL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Toast.makeText(mFragment.getContext(), "Reset", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        backupBL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Toast.makeText(mFragment.getContext(), "Backup", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        restoreBL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                Toast.makeText(mFragment.getContext(), "Restore", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setSwitchPreferenceStates() {
        String blString = Settings.Secure.getString(mFragment.getActivity().getContentResolver(), "icon_blacklist");
        if (blString == null) blString = "";

        ArrayList<String> blItems = new ArrayList<>(Arrays.asList(blString.split("[,]")));

        for (int i = 0; i < mFragment.getPreferenceScreen().getRootAdapter().getCount(); i++) {
            Object o = mFragment.getPreferenceScreen().getRootAdapter().getItem(i);

            if (o instanceof SwitchPreference) {
                SwitchPreference pref = (SwitchPreference) o;

                pref.setChecked(true);

                if (!blString.isEmpty()) {
                    String key = pref.getKey();

                    if (key != null) {
                        ArrayList<String> keyItems = new ArrayList<>(Arrays.asList(key.split("[,]")));

                        for (String s : keyItems) {
                            if (blItems.contains(s)) {
                                pref.setChecked(false);
                            }
                        }
                    }
                }
            }
        }
    }

    private void switchPreferenceListeners() {

    }

}
