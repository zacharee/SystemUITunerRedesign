package com.zacharee1.systemuituner.fragmenthelpers;

import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.util.SettingsUtils;

public class StatbarHelper extends BaseHelper
{

    public StatbarHelper(ItemDetailFragment fragment) {
        super(fragment);

        preferenceListeners();
        setSwitchPreferenceStates();
        switchPreferenceListeners();
    }

    private void preferenceListeners() {
        Preference resetBL = findPreference("reset_blacklist");
        Preference backupBL = findPreference("backup_blacklist");
        Preference restoreBL = findPreference("restore_blacklist");

        resetBL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                SettingsUtils.writeSecure(getContext(), "icon_blacklist", "");
                setSwitchPreferenceStates();
                return true;
            }
        });

        backupBL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                String currentBL = Settings.Secure.getString(getContext().getContentResolver(), "icon_blacklist");
                SettingsUtils.writeGlobal(getContext(), "icon_blacklist_backup", currentBL);
                setSwitchPreferenceStates();
                return true;
            }
        });

        restoreBL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                String backupBL = Settings.Global.getString(getContext().getContentResolver(), "icon_blacklist_backup");
                SettingsUtils.writeSecure(getContext(), "icon_blacklist", backupBL);
                setSwitchPreferenceStates();
                return true;
            }
        });
    }

    private void setSwitchPreferenceStates() {
        SettingsUtils.shouldSetSwitchChecked(getFragment());
    }

    private void switchPreferenceListeners() {
        for (int i = 0; i < getPreferenceScreen().getRootAdapter().getCount(); i++) {
            Object o = getPreferenceScreen().getRootAdapter().getItem(i);

            if (o instanceof SwitchPreference && !((SwitchPreference)o).getTitle().toString().toLowerCase().contains("high brightness warning")) {
                final SwitchPreference pref = (SwitchPreference) o;

                pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o)
                    {
                        String key = preference.getKey();
                        boolean value = Boolean.valueOf(o.toString());

                        SettingsUtils.changeBlacklist(key, value, getContext());
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {

    }
}
