package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.SettingsUtils;

import java.util.ArrayList;

public class MiscHelper
{
    private static final int TWILIGHT_MODE_INACTIVE = 0;
    private static final int TWILIGHT_MODE_OVERRIDE = 1;
    private static final int TWILIGHT_MODE_AUTO = 2;
    private static final int TWILIGHT_MODE_AUTO_OVERRIDE = 4;

    private final SharedPreferences mSharedPreferences;

    private ItemDetailFragment mFragment;

    public MiscHelper(ItemDetailFragment fragment) {
        mFragment = fragment;
        mSharedPreferences = mFragment.getPreferenceManager().getSharedPreferences();

        showCustomSettings();
        setGlobalSwitchStates();
        setSecureSwitchStates();
        setSystemSwitchStates();
        setNightModeSwitchStates();
        setEditTextStates();
    }

    private boolean showingCustomSettings() {
        return mSharedPreferences.getBoolean("allow_custom_settings_input", false);
    }

    private void showCustomSettings() {
        PreferenceCategory customSettings = (PreferenceCategory) mFragment.findPreference("custom_settings_values");
        if (!mSharedPreferences.getBoolean("allow_custom_settings_input", false)) {
            customSettings.setEnabled(false);

            for (int i = 0; i < customSettings.getPreferenceCount(); i++) {
                Preference preference = customSettings.getPreference(i);
                preference.setSummary(R.string.enable_in_settings);
            }
        }
    }

    private void setGlobalSwitchStates() {
        ArrayList<SwitchPreference> preferences = new ArrayList<SwitchPreference>() {{
            add((SwitchPreference) mFragment.findPreference("heads_up_notifications_enabled"));
            add((SwitchPreference) mFragment.findPreference("audio_safe_volume_state"));
        }};

        for (SwitchPreference preference : preferences) {
            final String key = preference.getKey();
            preference.setChecked(Settings.Global.getInt(mFragment.getContext().getContentResolver(), key, 1) == 1);
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.writeGlobal(mFragment.getContext(), key, Boolean.valueOf(o.toString()) ? "1" : "0");
                    return true;
                }
            });
        }
    }

    private void setSecureSwitchStates() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            PreferenceCategory category = (PreferenceCategory) mFragment.findPreference("power_notification_controls");
            category.setEnabled(false);

            for (int i = 0; i < category.getPreferenceCount(); i++) {
                SwitchPreference preference = (SwitchPreference) category.getPreference(i);
                preference.setChecked(false);
                preference.setSummary(R.string.requires_nougat);
            }
        }

        ArrayList<SwitchPreference> preferences = new ArrayList<SwitchPreference>() {{
           if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) add((SwitchPreference) mFragment.findPreference("sysui_show_full_zen"));
           if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) add((SwitchPreference) mFragment.findPreference("clock_seconds"));
           if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) add((SwitchPreference) mFragment.findPreference("show_importance_slider"));
        }};

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            SwitchPreference preference = (SwitchPreference) mFragment.findPreference("clock_seconds");
            preference.setEnabled(false);
            preference.setChecked(false);
            preference.setSummary(R.string.requires_nougat);

            preference = (SwitchPreference) mFragment.findPreference("sysui_show_full_zen");
            preference.setEnabled(false);
            preference.setChecked(false);
            preference.setSummary(R.string.requires_nougat);
        }

        for (SwitchPreference preference : preferences) {
            final String key = preference.getKey();
            preference.setChecked(Settings.Secure.getInt(mFragment.getContext().getContentResolver(), key, 0) == 1);
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.writeSecure(mFragment.getContext(), key, Boolean.valueOf(o.toString()) ? "1" : "0");
                    return true;
                }
            });
        }
    }

    private void setSystemSwitchStates() {
        ArrayList<SwitchPreference> preferences = new ArrayList<SwitchPreference>() {{
            add((SwitchPreference) mFragment.findPreference("status_bar_show_battery_percent"));
        }};

        for (SwitchPreference preference : preferences) {
            final String key = preference.getKey();
            preference.setChecked(Settings.System.getInt(mFragment.getContext().getContentResolver(), key, 0) == 1);
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.writeSystem(mFragment.getContext(), key, Boolean.valueOf(o.toString()) ? "1" : "0");
                    return true;
                }
            });
        }
    }

    private void setNightModeSwitchStates() {
        final SwitchPreference auto = (SwitchPreference) mFragment.findPreference("night_mode_auto");
        final SwitchPreference override = (SwitchPreference) mFragment.findPreference("night_mode_override");
        final SwitchPreference tint = (SwitchPreference) mFragment.findPreference("tuner_night_mode_adjust_tint");

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
        {
            tint.setChecked(Settings.Secure.getInt(mFragment.getContext().getContentResolver(), "night_mode_adjust_tint", 0) == 1);

            int current = Settings.Secure.getInt(mFragment.getContext().getContentResolver(), "twilight_mode", 0);

            switch (current)
            {
                case TWILIGHT_MODE_INACTIVE:
                    auto.setChecked(false);
                    override.setChecked(false);
                    break;
                case TWILIGHT_MODE_OVERRIDE:
                    auto.setChecked(false);
                    override.setChecked(true);
                    break;
                case TWILIGHT_MODE_AUTO:
                    auto.setChecked(true);
                    override.setChecked(false);
                    break;
                case TWILIGHT_MODE_AUTO_OVERRIDE:
                    auto.setChecked(true);
                    override.setChecked(true);
                    break;
            }

            tint.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.writeSecure(mFragment.getContext(), "night_mode_adjust_tint", Boolean.valueOf(o.toString()) ? "1" : "0");
                    return true;
                }
            });

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            PreferenceCategory category = (PreferenceCategory) mFragment.findPreference("night_mode_settings");
            category.setTitle(R.string.night_display);
            category.removePreference(tint);

            override.setChecked(Settings.Secure.getInt(mFragment.getContext().getContentResolver(), "night_display_activated", 0) == 1);
            override.setTitle(R.string.night_display_activated);
            auto.setChecked(Settings.Secure.getInt(mFragment.getContext().getContentResolver(), "night_display_auto", 0) == 1);
            auto.setTitle(R.string.night_display_auto);
        } else {
            PreferenceCategory category = (PreferenceCategory) mFragment.findPreference("night_mode_settings");
            category.setEnabled(false);

            for (int i = 0; i < category.getPreferenceCount(); i++) {
                SwitchPreference preference = (SwitchPreference) category.getPreference(i);
                preference.setChecked(false);
                preference.setSummary(R.string.requires_nougat);
            }
        }

        auto.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) evalNightModeStates(Boolean.valueOf(o.toString()), override.isChecked());
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) SettingsUtils.writeSecure(mFragment.getContext(), "night_display_auto", Boolean.valueOf(o.toString()) ? "1" : "0");
                return true;
            }
        });

        override.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) evalNightModeStates(auto.isChecked(), Boolean.valueOf(o.toString()));
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) SettingsUtils.writeSecure(mFragment.getContext(), "night_display_activated", Boolean.valueOf(o.toString()) ? "1" : "0");
                return true;
            }
        });
    }

    private void evalNightModeStates(boolean auto, boolean override) {
        int val = 0;

        if (override && !auto) val = 1;
        else if (!override && auto) val = 2;
        else if (override) val = 4;

        SettingsUtils.writeSecure(mFragment.getContext(), "twilight_mode", val + "");
    }

    private void setEditTextStates() {
        ArrayList<EditTextPreference> preferences = new ArrayList<EditTextPreference>() {{
            add((EditTextPreference) mFragment.findPreference("animator_duration_scale"));
            add((EditTextPreference) mFragment.findPreference("transition_animation_scale"));
            add((EditTextPreference) mFragment.findPreference("window_animation_scale"));
            if (showingCustomSettings())
            {
                add((EditTextPreference) mFragment.findPreference("global_settings"));
                add((EditTextPreference) mFragment.findPreference("secure_settings"));
                add((EditTextPreference) mFragment.findPreference("system_settings"));
            }
        }};

        for (EditTextPreference preference : preferences) {
            final String key = preference.getKey();
            preference.setPersistent(false);

            if (key.contains("anim")) {
                preference.setSummary(Settings.Global.getFloat(mFragment.getContext().getContentResolver(), key, 1.0f) + "");
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o)
                    {
                        SettingsUtils.writeGlobal(mFragment.getContext(), key, o.toString());
                        preference.setSummary(o.toString());
                        return true;
                    }
                });
            } else if (key.contains("settings")) {
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o)
                    {
                        String value = o.toString();
                        String[] keyVal = value.split("[ ]");

                        String putKey = "";
                        String putVal = "";

                        switch (keyVal.length) {
                            case 2:
                                putVal = keyVal[1];
                            case 1:
                                putKey = keyVal[0];
                                break;
                        }

                        switch (key) {
                            case "global_settings":
                                SettingsUtils.writeGlobal(mFragment.getContext(), putKey, putVal);
                                break;
                            case "secure_settings":
                                SettingsUtils.writeSecure(mFragment.getContext(), putKey, putVal);
                                break;
                            case "system_settings":
                                SettingsUtils.writeSystem(mFragment.getContext(), putKey, putVal);
                                break;
                        }

                        return true;
                    }
                });
            }
        }
    }
}
