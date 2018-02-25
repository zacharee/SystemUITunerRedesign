package com.zacharee1.systemuituner.fragmenthelpers

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.SwitchPreference
import android.provider.Settings
import com.zacharee1.sliderpreferenceembedded.SliderPreferenceEmbedded
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import com.zacharee1.systemuituner.util.SettingsUtils
import java.util.*

class MiscHelper(fragment: ItemDetailFragment) : BaseHelper(fragment) {

    private val mSharedPreferences: SharedPreferences = preferenceManager.sharedPreferences

    init {

        showCustomSettings()
        setGlobalSwitchStates()
        setSecureSwitchStates()
        setSystemSwitchStates()
        setNightModeSwitchStates()
        setEditTextStates()
        setUpAnimationScales()
        setUpSnoozeStuff()
    }

    private fun showingCustomSettings(): Boolean {
        return mSharedPreferences.getBoolean(ALLOW_CUSTOM_INPUT, false)
    }

    private fun showCustomSettings() {
        val customSettings = findPreference(CUSTOM_SETTINGS_VALUES) as PreferenceCategory
        if (!mSharedPreferences.getBoolean(ALLOW_CUSTOM_INPUT, false)) {
            customSettings.isEnabled = false

            (0 until customSettings.preferenceCount)
                    .map { customSettings.getPreference(it) }
                    .forEach { it.setSummary(R.string.enable_in_settings) }
        }
    }

    private fun setGlobalSwitchStates() {
        val preferences = object : ArrayList<SwitchPreference>() {
            init {
                add(findPreference(HUD_ENABLED) as SwitchPreference)
                add(findPreference(AUDIO_SAFE) as SwitchPreference)
            }
        }

        for (preference in preferences) {
            val key = preference.key
            preference.isChecked = Settings.Global.getInt(context.contentResolver, key, 1) == 1
            preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                SettingsUtils.writeGlobal(context, key, if (java.lang.Boolean.valueOf(o.toString())) "1" else "0")
                true
            }
        }
    }

    private fun setSecureSwitchStates() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val category = findPreference(POWER_NOTIFICATION_CONTROLS) as PreferenceCategory
            category.isEnabled = false

            for (i in 0 until category.preferenceCount) {
                val preference = findPreference(SHOW_IMPORTANCE_SLIDER) as SwitchPreference
                preference.isChecked = false
                preference.setSummary(if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) R.string.requires_nougat else R.string.safe_mode_android_o)
            }
        }

        val preferences = object : ArrayList<SwitchPreference>() {
            init {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) add(findPreference(SHOW_ZEN) as SwitchPreference)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) add(findPreference(CLOCK_SECONDS) as SwitchPreference)
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) add(findPreference(SHOW_IMPORTANCE_SLIDER) as SwitchPreference)
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            var preference = findPreference(CLOCK_SECONDS) as SwitchPreference
            preference.isEnabled = false
            preference.isChecked = false
            preference.setSummary(R.string.requires_nougat)

            preference = findPreference(SHOW_ZEN) as SwitchPreference
            preference.isEnabled = false
            preference.isChecked = false
            preference.setSummary(R.string.requires_nougat)
        }

        for (preference in preferences) {
            val key = preference.key
            preference.isChecked = Settings.Secure.getInt(context.contentResolver, key, 0) == 1
            preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                SettingsUtils.writeSecure(context, key, if (java.lang.Boolean.valueOf(o.toString())) "1" else "0")
                true
            }
        }
    }

    private fun setSystemSwitchStates() {
        val preferences = object : ArrayList<SwitchPreference>() {
            init {
                add(findPreference(STATUS_BAR_BATTERY) as SwitchPreference)
            }
        }

        for (preference in preferences) {
            val key = preference.key
            preference.isChecked = Settings.System.getInt(context.contentResolver, key, 0) == 1
            preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                SettingsUtils.writeSystem(context, key, if (java.lang.Boolean.valueOf(o.toString())) "1" else "0")
                true
            }
        }
    }

    private fun setNightModeSwitchStates() {
        val auto = findPreference(NIGHT_MODE_AUTO) as SwitchPreference
        val override = findPreference(NIGHT_MODE_OVERRIDE) as SwitchPreference
        val tint = findPreference(TUNER_NIGHT_MODE_TINT) as SwitchPreference

        when {
            Build.VERSION.SDK_INT == Build.VERSION_CODES.N -> {
                tint.isChecked = Settings.Secure.getInt(context.contentResolver, NIGHT_MODE_TINT, 0) == 1

                val current = Settings.Secure.getInt(context.contentResolver, TWILIGHT_MODE, 0)

                when (current) {
                    TWILIGHT_MODE_INACTIVE -> {
                        auto.isChecked = false
                        override.isChecked = false
                    }
                    TWILIGHT_MODE_OVERRIDE -> {
                        auto.isChecked = false
                        override.isChecked = true
                    }
                    TWILIGHT_MODE_AUTO -> {
                        auto.isChecked = true
                        override.isChecked = false
                    }
                    TWILIGHT_MODE_AUTO_OVERRIDE -> {
                        auto.isChecked = true
                        override.isChecked = true
                    }
                }

                tint.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                    SettingsUtils.writeSecure(context, NIGHT_MODE_TINT, if (java.lang.Boolean.valueOf(o.toString())) "1" else "0")
                    true
                }

            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 -> {
                val category = findPreference(NIGHT_MODE_SETTINGS) as PreferenceCategory
                category.setTitle(R.string.night_display)
                category.removePreference(tint)

                override.isChecked = Settings.Secure.getInt(context.contentResolver, NIGHT_DISPLAY_ACTIVATED, 0) == 1
                override.setTitle(R.string.night_display_activated)
                auto.isChecked = Settings.Secure.getInt(context.contentResolver, NIGHT_DISPLAY_AUTO, 0) == 1
                auto.setTitle(R.string.night_display_auto)

                try {
                    @SuppressLint("PrivateApi") val InternalBool = Class.forName("com.android.internal.R\$bool")

                    val nightDisplayAvailable = InternalBool.getField("config_nightDisplayAvailable")
                    val id = nightDisplayAvailable.getInt(null)

                    if (!Resources.getSystem().getBoolean(id)) {
                        category.isEnabled = false

                        for (i in 0 until category.preferenceCount) {
                            val preference = category.getPreference(i) as SwitchPreference
                            preference.isChecked = false
                            preference.setSummary(R.string.night_display_not_avail)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            else -> {
                val category = findPreference(NIGHT_MODE_SETTINGS) as PreferenceCategory
                category.isEnabled = false

                for (i in 0 until category.preferenceCount) {
                    val preference = category.getPreference(i) as SwitchPreference
                    preference.isChecked = false
                    preference.setSummary(R.string.requires_nougat)
                }
            }
        }

        auto.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
                evalNightModeStates(java.lang.Boolean.valueOf(o.toString()), override.isChecked)
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) SettingsUtils.writeSecure(context, NIGHT_DISPLAY_AUTO, if (java.lang.Boolean.valueOf(o.toString())) "1" else "0")
            true
        }

        override.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
                evalNightModeStates(auto.isChecked, java.lang.Boolean.valueOf(o.toString()))
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) SettingsUtils.writeSecure(context, NIGHT_DISPLAY_ACTIVATED, if (java.lang.Boolean.valueOf(o.toString())) "1" else "0")
            true
        }
    }

    private fun evalNightModeStates(auto: Boolean, override: Boolean) {
        var `val` = 0

        if (override && !auto)
            `val` = 1
        else if (!override && auto)
            `val` = 2
        else if (override) `val` = 4

        SettingsUtils.writeSecure(context, TWILIGHT_MODE, `val`.toString() + "")
    }

    private fun setEditTextStates() {
        val preferences = object : ArrayList<EditTextPreference>() {
            init {
                if (showingCustomSettings()) {
                    add(findPreference(GLOBAL_SETTINGS) as EditTextPreference)
                    add(findPreference(SECURE_SETTINGS) as EditTextPreference)
                    add(findPreference(SYSTEM_SETTINGS) as EditTextPreference)
                }
            }
        }

        for (preference in preferences) {
            val key = preference.key
            preference.isPersistent = false

            if (key.contains("settings")) {
                preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                    val value = o.toString()
                    val keyVal = value.split(" ")

                    var putKey = ""
                    var putVal = ""

                    when (keyVal.size) {
                        2 -> {
                            putVal = keyVal[1]
                            putKey = keyVal[0]
                        }
                        1 -> putKey = keyVal[0]
                    }

                    when (key) {
                        GLOBAL_SETTINGS -> SettingsUtils.writeGlobal(context, putKey, putVal)
                        SECURE_SETTINGS -> SettingsUtils.writeSecure(context, putKey, putVal)
                        SYSTEM_SETTINGS -> SettingsUtils.writeSystem(context, putKey, putVal)
                    }

                    true
                }
            }
        }
    }

    private fun setUpAnimationScales() {
        val duration = findPreference(Settings.Global.ANIMATOR_DURATION_SCALE) as SliderPreferenceEmbedded
        val transition = findPreference(Settings.Global.TRANSITION_ANIMATION_SCALE) as SliderPreferenceEmbedded
        val window = findPreference(Settings.Global.WINDOW_ANIMATION_SCALE) as SliderPreferenceEmbedded

        val durScale = Settings.Global.getFloat(activity?.contentResolver, duration.key, 1.0f)
        val tranScale = Settings.Global.getFloat(activity?.contentResolver, transition.key, 1.0f)
        val winScale = Settings.Global.getFloat(activity?.contentResolver, window.key, 1.0f)

        duration.progress = (durScale * 100).toInt()
        transition.progress = (tranScale * 100).toInt()
        window.progress = (winScale * 100).toInt()

        val listener = Preference.OnPreferenceChangeListener { preference, o ->
            SettingsUtils.writeGlobal(context, preference.key, (java.lang.Float.valueOf(o.toString())!! / 100).toString())
            true
        }

        duration.onPreferenceChangeListener = listener
        transition.onPreferenceChangeListener = listener
        window.onPreferenceChangeListener = listener
    }

    private fun setUpSnoozeStuff() {
        val category = findPreference("notifs_snooze") as PreferenceCategory
        val summary = findPreference("notifs_snooze_desc")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) {
            category.isEnabled = false
            summary?.setSummary(R.string.requires_8_1)
        } else {
            val def = findPreference("default_time") as EditTextPreference
            val a = findPreference("time_a") as EditTextPreference
            val b = findPreference("time_b") as EditTextPreference
            val c = findPreference("time_c") as EditTextPreference
            val d = findPreference("time_d") as EditTextPreference

            val times = parseSnoozeTimes()

            def.text = times[0]
            a.text = times[1]
            b.text = times[2]
            c.text = times[3]
            d.text = times[4]

            def.summary = times[0]
            a.summary = times[1]
            b.summary = times[2]
            c.summary = times[3]
            d.summary = times[4]

            val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
                val toSave = ArrayList<String>()
                toSave.add(def.text)
                toSave.add(a.text)
                toSave.add(b.text)
                toSave.add(c.text)
                toSave.add(d.text)

                var index = 0
                when (preference.key) {
                    "default_time" -> index = 0
                    "time_a" -> index = 1
                    "time_b" -> index = 2
                    "time_c" -> index = 3
                    "time_d" -> index = 4
                }
                toSave[index] = newValue.toString()

                preference.summary = newValue.toString()

                saveSnoozeTimes(toSave)

                true
            }

            def.onPreferenceChangeListener = listener
            a.onPreferenceChangeListener = listener
            b.onPreferenceChangeListener = listener
            c.onPreferenceChangeListener = listener
            d.onPreferenceChangeListener = listener
        }
    }

    private fun saveSnoozeTimes(toSave: ArrayList<String>) {
        val base = "default=" + toSave[0] + ",options_array=" + toSave[1] + ":" + toSave[2] + ":" + toSave[3] + ":" + toSave[4]
        SettingsUtils.writeGlobal(context, "notification_snooze_options", base)
    }

    private fun parseSnoozeTimes(): ArrayList<String> {
        val ret = ArrayList<String>()
        val saved = Settings.Global.getString(context.contentResolver, "notification_snooze_options")

        if (saved == null || saved.isEmpty()) {
            ret.add("60")
            ret.add("15")
            ret.add("30")
            ret.add("60")
            ret.add("120")
        } else {
            val parts = saved.split(",")
            val def = parts[0].split("=")[1]
            val options = parts[1].split("=")[1].split(":")

            ret.add(def)
            ret.addAll(options)
        }

        return ret
    }

    override fun onDestroy() {

    }

    companion object {
        const val ALLOW_CUSTOM_INPUT = "allow_custom_settings_input"
        const val CUSTOM_SETTINGS_VALUES = "custom_settings_values"
        const val HUD_ENABLED = "heads_up_notifications_enabled"
        const val AUDIO_SAFE = "audio_safe_volume_state"
        const val POWER_NOTIFICATION_CONTROLS = "power_notification_controls"
        const val SHOW_IMPORTANCE_SLIDER = "show_importance_slider"
        const val SHOW_ZEN = "sysui_show_full_zen"
        const val CLOCK_SECONDS = "clock_seconds"
        const val STATUS_BAR_BATTERY = "status_bar_show_battery_percent"
        const val NIGHT_MODE_AUTO = "night_mode_auto"
        const val NIGHT_MODE_OVERRIDE = "night_mode_override"
        const val NIGHT_MODE_TINT = "night_mode_adjust_tint"
        const val TUNER_NIGHT_MODE_TINT = "tuner_night_mode_adjust_tint"
        const val TWILIGHT_MODE = "twilight_mode"
        const val NIGHT_DISPLAY_ACTIVATED = "night_display_activated"
        const val NIGHT_DISPLAY_AUTO = "night_display_auto"
        const val NIGHT_MODE_SETTINGS = "night_mode_settings"
        const val GLOBAL_SETTINGS = "global_settings"
        const val SECURE_SETTINGS = "secure_settings"
        const val SYSTEM_SETTINGS = "system_settings"

        private const val TWILIGHT_MODE_INACTIVE = 0
        private const val TWILIGHT_MODE_OVERRIDE = 1
        private const val TWILIGHT_MODE_AUTO = 2
        private const val TWILIGHT_MODE_AUTO_OVERRIDE = 4
    }
}
