package com.zacharee1.systemuituner.fragmenthelpers;

import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.util.SettingsUtils;

public class StatbarHelper extends BaseHelper
{
    public static final String RESET_BLACKLIST = "reset_blacklist";
    public static final String BACKUP_BLACKLIST = "backup_blacklist";
    public static final String RESTORE_BLACKLIST = "restore_blacklist";
    public static final String ICON_BLACKLIST = "icon_blacklist";
    public static final String ICON_BLACKLIST_BACKUP = "icon_blacklist_backup";

    public StatbarHelper(ItemDetailFragment fragment) {
        super(fragment);

        preferenceListeners();
        setSwitchPreferenceStates();
        switchPreferenceListeners();
    }

    private void preferenceListeners() {
        Preference resetBL = findPreference(RESET_BLACKLIST);
        Preference backupBL = findPreference(BACKUP_BLACKLIST);
        Preference restoreBL = findPreference(RESTORE_BLACKLIST);

        resetBL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                SettingsUtils.writeSecure(getContext(), ICON_BLACKLIST, "");
                setSwitchPreferenceStates();
                return true;
            }
        });

        backupBL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                String currentBL = Settings.Secure.getString(getContext().getContentResolver(), ICON_BLACKLIST);
                SettingsUtils.writeGlobal(getContext(), ICON_BLACKLIST_BACKUP, currentBL);
                setSwitchPreferenceStates();
                return true;
            }
        });

        restoreBL.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                String backupBL = Settings.Global.getString(getContext().getContentResolver(), ICON_BLACKLIST_BACKUP);
                SettingsUtils.writeSecure(getContext(), ICON_BLACKLIST, backupBL);
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

            if (o instanceof SwitchPreference) {
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
