package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.sliderpreferenceembedded.SliderPreferenceEmbedded;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.util.SettingsUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MiscHelper extends BaseHelper
{
    public static final String ALLOW_CUSTOM_INPUT = "allow_custom_settings_input";
    public static final String CUSTOM_SETTINGS_VALUES = "custom_settings_values";
    public static final String HUD_ENABLED = "heads_up_notifications_enabled";
    public static final String AUDIO_SAFE = "audio_safe_volume_state";
    public static final String POWER_NOTIFICATION_CONTROLS = "power_notification_controls";
    public static final String SHOW_IMPORTANCE_SLIDER = "show_importance_slider";
    public static final String SHOW_ZEN = "sysui_show_full_zen";
    public static final String CLOCK_SECONDS = "clock_seconds";
    public static final String STATUS_BAR_BATTERY = "status_bar_show_battery_percent";
    public static final String NIGHT_MODE_AUTO = "night_mode_auto";
    public static final String NIGHT_MODE_OVERRIDE = "night_mode_override";
    public static final String NIGHT_MODE_TINT = "night_mode_adjust_tint";
    public static final String TUNER_NIGHT_MODE_TINT = "tuner_night_mode_adjust_tint";
    public static final String TWILIGHT_MODE = "twilight_mode";
    public static final String NIGHT_DISPLAY_ACTIVATED = "night_display_activated";
    public static final String NIGHT_DISPLAY_AUTO = "night_display_auto";
    public static final String NIGHT_MODE_SETTINGS = "night_mode_settings";
    public static final String GLOBAL_SETTINGS = "global_settings";
    public static final String SECURE_SETTINGS = "secure_settings";
    public static final String SYSTEM_SETTINGS = "system_settings";

    private static final int TWILIGHT_MODE_INACTIVE = 0;
    private static final int TWILIGHT_MODE_OVERRIDE = 1;
    private static final int TWILIGHT_MODE_AUTO = 2;
    private static final int TWILIGHT_MODE_AUTO_OVERRIDE = 4;

    private final SharedPreferences mSharedPreferences;
    
    public MiscHelper(ItemDetailFragment fragment) {
        super(fragment);
        
        mSharedPreferences = getPreferenceManager().getSharedPreferences();

        showCustomSettings();
        setGlobalSwitchStates();
        setSecureSwitchStates();
        setSystemSwitchStates();
        setNightModeSwitchStates();
        setEditTextStates();
        setUpAnimationScales();
    }

    private boolean showingCustomSettings() {
        return mSharedPreferences.getBoolean(ALLOW_CUSTOM_INPUT, false);
    }

    private void showCustomSettings() {
        PreferenceCategory customSettings = (PreferenceCategory) findPreference(CUSTOM_SETTINGS_VALUES);
        if (!mSharedPreferences.getBoolean(ALLOW_CUSTOM_INPUT, false)) {
            customSettings.setEnabled(false);

            for (int i = 0; i < customSettings.getPreferenceCount(); i++) {
                Preference preference = customSettings.getPreference(i);
                preference.setSummary(R.string.enable_in_settings);
            }
        }
    }

    private void setGlobalSwitchStates() {
        ArrayList<SwitchPreference> preferences = new ArrayList<SwitchPreference>() {{
            add((SwitchPreference) findPreference(HUD_ENABLED));
            add((SwitchPreference) findPreference(AUDIO_SAFE));
        }};

        for (SwitchPreference preference : preferences) {
            final String key = preference.getKey();
            preference.setChecked(Settings.Global.getInt(getContext().getContentResolver(), key, 1) == 1);
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.writeGlobal(getContext(), key, Boolean.valueOf(o.toString()) ? "1" : "0");
                    return true;
                }
            });
        }
    }

    private void setSecureSwitchStates() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PreferenceCategory category = (PreferenceCategory) findPreference(POWER_NOTIFICATION_CONTROLS);
            category.setEnabled(false);

            for (int i = 0; i < category.getPreferenceCount(); i++) {
                SwitchPreference preference = (SwitchPreference) findPreference(SHOW_IMPORTANCE_SLIDER);
                preference.setChecked(false);
                preference.setSummary(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M ? R.string.requires_nougat : R.string.safe_mode_android_o);
            }
        }

        ArrayList<SwitchPreference> preferences = new ArrayList<SwitchPreference>() {{
           if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) add((SwitchPreference) findPreference(SHOW_ZEN));
           if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) add((SwitchPreference) findPreference(CLOCK_SECONDS));
           if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) add((SwitchPreference) findPreference(SHOW_IMPORTANCE_SLIDER));
        }};

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            SwitchPreference preference = (SwitchPreference) findPreference(CLOCK_SECONDS);
            preference.setEnabled(false);
            preference.setChecked(false);
            preference.setSummary(R.string.requires_nougat);

            preference = (SwitchPreference) findPreference(SHOW_ZEN);
            preference.setEnabled(false);
            preference.setChecked(false);
            preference.setSummary(R.string.requires_nougat);
        }

        for (SwitchPreference preference : preferences) {
            final String key = preference.getKey();
            preference.setChecked(Settings.Secure.getInt(getContext().getContentResolver(), key, 0) == 1);
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.writeSecure(getContext(), key, Boolean.valueOf(o.toString()) ? "1" : "0");
                    return true;
                }
            });
        }
    }

    private void setSystemSwitchStates() {
        ArrayList<SwitchPreference> preferences = new ArrayList<SwitchPreference>() {{
            add((SwitchPreference) findPreference(STATUS_BAR_BATTERY));
        }};

        for (SwitchPreference preference : preferences) {
            final String key = preference.getKey();
            preference.setChecked(Settings.System.getInt(getContext().getContentResolver(), key, 0) == 1);
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.writeSystem(getContext(), key, Boolean.valueOf(o.toString()) ? "1" : "0");
                    return true;
                }
            });
        }
    }

    private void setNightModeSwitchStates() {
        final SwitchPreference auto = (SwitchPreference) findPreference(NIGHT_MODE_AUTO);
        final SwitchPreference override = (SwitchPreference) findPreference(NIGHT_MODE_OVERRIDE);
        final SwitchPreference tint = (SwitchPreference) findPreference(TUNER_NIGHT_MODE_TINT);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
        {
            tint.setChecked(Settings.Secure.getInt(getContext().getContentResolver(), NIGHT_MODE_TINT, 0) == 1);

            int current = Settings.Secure.getInt(getContext().getContentResolver(), TWILIGHT_MODE, 0);

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
                    SettingsUtils.writeSecure(getContext(), NIGHT_MODE_TINT, Boolean.valueOf(o.toString()) ? "1" : "0");
                    return true;
                }
            });

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            PreferenceCategory category = (PreferenceCategory) findPreference(NIGHT_MODE_SETTINGS);
            category.setTitle(R.string.night_display);
            category.removePreference(tint);

            override.setChecked(Settings.Secure.getInt(getContext().getContentResolver(), NIGHT_DISPLAY_ACTIVATED, 0) == 1);
            override.setTitle(R.string.night_display_activated);
            auto.setChecked(Settings.Secure.getInt(getContext().getContentResolver(), NIGHT_DISPLAY_AUTO, 0) == 1);
            auto.setTitle(R.string.night_display_auto);

            try {
                Class<?> InternalBool = Class.forName("com.android.internal.R$bool");

                Field nightDisplayAvailable = InternalBool.getField("config_nightDisplayAvailable");
                int id = nightDisplayAvailable.getInt(null);

                if (!Resources.getSystem().getBoolean(id)) {
                    category.setEnabled(false);

                    for (int i = 0; i < category.getPreferenceCount(); i++) {
                        SwitchPreference preference = (SwitchPreference) category.getPreference(i);
                        preference.setChecked(false);
                        preference.setSummary(R.string.night_display_not_avail);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PreferenceCategory category = (PreferenceCategory) findPreference(NIGHT_MODE_SETTINGS);
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
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) SettingsUtils.writeSecure(getContext(), NIGHT_DISPLAY_AUTO, Boolean.valueOf(o.toString()) ? "1" : "0");
                return true;
            }
        });

        override.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) evalNightModeStates(auto.isChecked(), Boolean.valueOf(o.toString()));
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) SettingsUtils.writeSecure(getContext(), NIGHT_DISPLAY_ACTIVATED, Boolean.valueOf(o.toString()) ? "1" : "0");
                return true;
            }
        });
    }

    private void evalNightModeStates(boolean auto, boolean override) {
        int val = 0;

        if (override && !auto) val = 1;
        else if (!override && auto) val = 2;
        else if (override) val = 4;

        SettingsUtils.writeSecure(getContext(), TWILIGHT_MODE, val + "");
    }

    private void setEditTextStates() {
        ArrayList<EditTextPreference> preferences = new ArrayList<EditTextPreference>() {{
//            add((EditTextPreference) findPreference("animator_duration_scale"));
//            add((EditTextPreference) findPreference("transition_animation_scale"));
//            add((EditTextPreference) findPreference("window_animation_scale"));
            if (showingCustomSettings())
            {
                add((EditTextPreference) findPreference(GLOBAL_SETTINGS));
                add((EditTextPreference) findPreference(SECURE_SETTINGS));
                add((EditTextPreference) findPreference(SYSTEM_SETTINGS));
            }
        }};

        for (EditTextPreference preference : preferences) {
            final String key = preference.getKey();
            preference.setPersistent(false);

            if (key.contains("settings")) {
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
                            case GLOBAL_SETTINGS:
                                SettingsUtils.writeGlobal(getContext(), putKey, putVal);
                                break;
                            case SECURE_SETTINGS:
                                SettingsUtils.writeSecure(getContext(), putKey, putVal);
                                break;
                            case SYSTEM_SETTINGS:
                                SettingsUtils.writeSystem(getContext(), putKey, putVal);
                                break;
                        }

                        return true;
                    }
                });
            }
        }
    }

    private void setUpAnimationScales() {
        SliderPreferenceEmbedded duration = (SliderPreferenceEmbedded) findPreference(Settings.Global.ANIMATOR_DURATION_SCALE);
        SliderPreferenceEmbedded transition = (SliderPreferenceEmbedded) findPreference(Settings.Global.TRANSITION_ANIMATION_SCALE);
        SliderPreferenceEmbedded window = (SliderPreferenceEmbedded) findPreference(Settings.Global.WINDOW_ANIMATION_SCALE);

        float durScale = Settings.Global.getFloat(getActivity().getContentResolver(), duration.getKey(), 1.0F);
        float tranScale = Settings.Global.getFloat(getActivity().getContentResolver(), transition.getKey(), 1.0F);
        float winScale = Settings.Global.getFloat(getActivity().getContentResolver(), window.getKey(), 1.0F);

        duration.setProgress((int)(durScale * 100));
        transition.setProgress((int)(tranScale * 100));
        window.setProgress((int)(winScale * 100));

        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                SettingsUtils.writeGlobal(getContext(), preference.getKey(), String.valueOf(Float.valueOf(o.toString()) / 100));
                return true;
            }
        };

        duration.setOnPreferenceChangeListener(listener);
        transition.setOnPreferenceChangeListener(listener);
        window.setOnPreferenceChangeListener(listener);
    }

    @Override
    public void onDestroy() {

    }
}
