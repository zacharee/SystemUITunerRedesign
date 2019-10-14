package com.zacharee1.systemuituner.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zacharee1.systemuituner.handlers.ImmersiveHandler
import com.zacharee1.systemuituner.misc.CustomBlacklistInfo
import java.util.*
import kotlin.collections.ArrayList

class PrefManager private constructor(private val context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: PrefManager? = null

        fun getInstance(context: Context): PrefManager {
            if (instance == null)
                instance = PrefManager(context.applicationContext)

            return instance!!
        }

        const val ALARM_DEMO = "alarm_demo"
        const val ALLOW_CUSTOM_SETTINGS_INPUT = "allow_custom_settings_input"
        const val AUDIO_SAFE = "audio_safe_volume_state"
        const val APP_IMMERSIVE = "app_immersive"
        const val BATTERY_CHARGING = "battery_charging"
        const val BLUETOOTH_ICON = "bluetooth_icon"
        const val CUSTOM_BLACKLIST_ITEMS = "custom_blacklist_items"
        const val DARK_MODE = "dark_mode"
        const val ERI_DEMO = "eri_demo"
        const val FIRST_START = "first_start"
        const val HIDE_WELCOME_SCREEN = "hide_welcome_screen"
        const val HIGH_BRIGHTNESS_WARNING = "shown_max_brightness_dialog"
        const val IMMERSIVE_APPS = "immersive_apps"
        const val IMMERSIVE_BLACKLIST = "immersive_blacklist"
        const val IMMERSIVE_TILE_MODE = "immersive_tile_mode"
        const val LOCATION_DEMO = "location_demo"
        const val MOBILE_FULLY_CONNECTED = "mobile_fully_connected"
        const val MOBILE_TYPE = "mobile_type"
        const val MUTE_DEMO = "mute_demo"
        const val NAVBAR_COLOR = "navigationbar_color"
        const val NAVBAR_CURRENT_COLOR = "navigationbar_current_color"
        const val NO_SIM = "no_sim"
        const val NOTIFICATION_SNOOZE_OPTIONS = "notification_snooze_options"
        const val QQS_COUNT = "sysui_qqs_count"
        const val QS_FANCY_ANIM = "sysui_qs_fancy_anim"
        const val SAFE_MODE = "safe_mode"
        const val SAFE_MODE_FANCY_ANIM = "safe_mode_fancy_anim"
        const val SAFE_MODE_HIGH_BRIGHTNESS_WARNING = "safe_mode_high_brightness_warning"
        const val SAFE_MODE_HEADER_COUNT = "safe_mode_header_count"
        const val SAFE_MODE_NOTIF = "show_safe_mode_notif"
        const val SAFE_MODE_ROW_COL = "safe_mode_row_col"
        const val SAFE_MODE_SNOOZE_OPTIONS = "safe_mode_snooze_options"
        const val SAFE_MODE_STATUS_BAR = "safe_mode_status_bar"
        const val SAFE_MODE_VOLUME_WARNING = "safe_mode_volume_warning"
        const val SELECTED_BATTERY_LEVEL = "selected_battery_level"
        const val SELECTED_MOBILE_STRENGTH = "selected_mobile_strength"
        const val SELECTED_TIME = "selected_time"
        const val SHOW_AIRPLANE = "show_airplane"
        const val SHOW_INTRO = "show_intro"
        const val SHOW_MOBILE = "show_mobile"
        const val SHOW_NOTIFICATIONS = "show_notifications"
        const val SHOW_WIFI = "show_wifi"
        const val SIM_COUNT = "sim_count"
        const val SPEAKERPHONE_DEMO = "speakerphone_demo"
        const val STATUS_BAR_STYLE = "status_bar_style"
        const val SYNC_DEMO = "sync_demo"
        const val TILE_COLUMN = "qs_tile_column"
        const val TILE_COLUMN_LANDSCAPE = "qs_tile_column_landscape"
        const val TILE_ROW = "qs_tile_row"
        const val TILE_ROW_LANDSCAPE = "qs_tile_row_landscape"
        const val TTY_DEMO = "tty_demo"
        const val TW_CLOCK_POSITION = "tw_clock_position"
        const val VOLUME_ICON = "volume_icon"
        const val WIFI_FULLY_CONNECTED = "wifi_fully_connected"
        const val WIFI_STRENGTH = "wifi_strength"
        const val FORCE_ENABLE_ADB = "force_enable_adb"
        const val GLOBAL_DARK_MODE = "ui_night_mode"
    }

    /**
     * Booleans
     */
    val allowCustomSettingsInput: Boolean
        get() = getBoolean(ALLOW_CUSTOM_SETTINGS_INPUT)
    val audioSafe: Boolean
        get() = getBoolean(AUDIO_SAFE, true)
    var appImmersive: Boolean
        get() = getBoolean(APP_IMMERSIVE)
        set(value) {
            putBoolean(APP_IMMERSIVE, value)
        }
    var darkMode: Boolean
        get() = getBoolean(DARK_MODE)
        set(value) {
            putBoolean(DARK_MODE, value)
        }
    val demoModeAlarm: Boolean
        get() = getBoolean(ALARM_DEMO)
    val demoModeBatteryCharging: Boolean
        get() = getBoolean(BATTERY_CHARGING)
    val demoModeMobileFullyConnected: Boolean
        get() = getBoolean(MOBILE_FULLY_CONNECTED)
    val demoModeNoSIM: Boolean
        get() = getBoolean(NO_SIM)
    val demoModeShowAirplaneMode: Boolean
        get() = getBoolean(SHOW_AIRPLANE)
    val demoModeShowEri: Boolean
        get() = getBoolean(ERI_DEMO)
    val demoModeShowLocation: Boolean
        get() = getBoolean(LOCATION_DEMO)
    val demoModeShowMobileIcon: Boolean
        get() = getBoolean(SHOW_MOBILE)
    val demoModeShowMute: Boolean
        get() = getBoolean(MUTE_DEMO)
    val demoModeShowNotifs: Boolean
        get() = getBoolean(SHOW_NOTIFICATIONS)
    val demoModeShowSpkerPhone: Boolean
        get() = getBoolean(SPEAKERPHONE_DEMO)
    val demoModeShowSync: Boolean
        get() = getBoolean(SYNC_DEMO)
    val demoModeShowTTY: Boolean
        get() = getBoolean(TTY_DEMO)
    val demoModeShowWiFi: Boolean
        get() = getBoolean(SHOW_WIFI)
    val demoModeWiFiFullyConnected: Boolean
        get() = getBoolean(WIFI_FULLY_CONNECTED)
    var firstStart: Boolean
        get() = getBoolean(FIRST_START, true)
        set(value) {
            putBoolean(FIRST_START, value)
        }
    var hideWelcomeScreen: Boolean
        get() = getBoolean(HIDE_WELCOME_SCREEN)
        set(value) {
            putBoolean(HIDE_WELCOME_SCREEN, value)
        }
    val highBrightnessWarning: Boolean
        get() = getBoolean(HIGH_BRIGHTNESS_WARNING, true)
    var immersiveBlacklist: Boolean
        get() = getBoolean(IMMERSIVE_BLACKLIST)
        set(value) {
            putBoolean(IMMERSIVE_BLACKLIST, value)
        }
    val qsFancyAnim: Boolean
        get() = getBoolean(QS_FANCY_ANIM, true)
    var safeMode: Boolean
        get() = getBoolean(SAFE_MODE)
        set(value) {
            putBoolean(SAFE_MODE, value)
        }
    val safeModeFancyAnim: Boolean
        get() = getBoolean(SAFE_MODE_FANCY_ANIM, true)
    val safeModeHbw: Boolean
        get() = getBoolean(SAFE_MODE_HIGH_BRIGHTNESS_WARNING, true)
    var safeModeHeaderCount: Boolean
        get() = getBoolean(SAFE_MODE_HEADER_COUNT, true)
                && !(context.checkSamsung() && Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1)
        set(value) {
            putBoolean(SAFE_MODE_HEADER_COUNT, value)
        }
    val safeModeNotif: Boolean
        get() = getBoolean(SAFE_MODE_NOTIF, true)
    var safeModeRowCol: Boolean
        get() = getBoolean(SAFE_MODE_ROW_COL, true)
        set(value) {
            putBoolean(SAFE_MODE_ROW_COL, value)
        }
    var safeModeSnoozeOptions: Boolean
        get() = getBoolean(SAFE_MODE_SNOOZE_OPTIONS, true)
        set(value) {
            putBoolean(SAFE_MODE_SNOOZE_OPTIONS, value)
        }
    val safeModeStatusBar: Boolean
        get() = getBoolean(SAFE_MODE_STATUS_BAR, true)
    val safeModeVolumeWarning: Boolean
        get() = getBoolean(SAFE_MODE_VOLUME_WARNING, true)
    var showIntro: Boolean
        get() = getBoolean(SHOW_INTRO, true)
        set(value) {
            putBoolean(SHOW_INTRO, value)
        }
    val forceEnableAdb: Boolean
        get() = getBoolean(FORCE_ENABLE_ADB, false)

    /**
     * Ints
     */
    val tileColumn: Int
        get() = getInt(TILE_COLUMN)
    val tileColumnLandscape: Int
        get() = getInt(TILE_COLUMN_LANDSCAPE)
    val tileRow: Int
        get() = getInt(TILE_ROW)
    val tileRowLandscape: Int
        get() = getInt(TILE_ROW_LANDSCAPE)
    val demoModeBatteryLevel: Int
        get() = getInt(SELECTED_BATTERY_LEVEL, 100)
    val demoModeMobileStrength: Int
        get() = getInt(SELECTED_MOBILE_STRENGTH, 4)
    val demoModeSIMCount: Int
        get() = getInt(SIM_COUNT, 0)
    val demoModeWiFiStrength: Int
        get() = getInt(WIFI_STRENGTH, 4)
    val qqsCount: Int
        get() = getInt(QQS_COUNT)

    /**
     * Longs
     */
    val demoModeSelectedTime: Long
        get() = getLong(SELECTED_TIME, System.currentTimeMillis())

    /**
     * Strings
     */
    val demoModeBluetoothIconState: String?
        get() = getString(BLUETOOTH_ICON, "hidden")
    val demoModeMobileType: String?
        get() = getString(MOBILE_TYPE, "lte")
    val demoModeStatusBarStyle: String?
        get() = getString(STATUS_BAR_STYLE, "default")
    val demoModeVolumeIcon: String?
        get() = getString(VOLUME_ICON, "hidden")
    var immersiveTileMode: String?
        get() = getString(IMMERSIVE_TILE_MODE, ImmersiveHandler.FULL)
        set(value) {
            putString(IMMERSIVE_TILE_MODE, value)
        }
    var notificationSnoozeOptions: String?
        get() = getString(NOTIFICATION_SNOOZE_OPTIONS, "")
        set(value) {
            putString(NOTIFICATION_SNOOZE_OPTIONS, value)
        }

    /**
     * Set<String>
     */
    var immersiveApps: Set<String>
        get() = getStringSet(IMMERSIVE_APPS)
        set(value) {
            putStringSet(IMMERSIVE_APPS, value)
        }

    /**
     * Other
     */
    var customBlacklistItems: ArrayList<CustomBlacklistInfo>
        get() {
            return Gson().fromJson<ArrayList<CustomBlacklistInfo>>(
                    getString(CUSTOM_BLACKLIST_ITEMS) ?: return ArrayList(),
                    object : TypeToken<ArrayList<CustomBlacklistInfo>>() {}.type
            )
        }
        set(value) {
            putString(CUSTOM_BLACKLIST_ITEMS, Gson().toJson(value))
        }


    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getString(key: String, def: String? = null): String? =
            prefs.getString(key, def)
    fun getStringSet(key: String, def: Set<String> = HashSet()): Set<String> =
            prefs.getStringSet(key, def)!!
    fun getInt(key: String, def: Int = -1) =
            prefs.getInt(key, def)
    fun getLong(key: String, def: Long = -1L) =
            prefs.getLong(key, def)
    fun getFloat(key: String, def: Float = -1f) =
            prefs.getFloat(key, def)
    fun getBoolean(key: String, def: Boolean = false) =
            prefs.getBoolean(key, def)

    fun contains(key: String) =
            prefs.contains(key)

    fun putString(key: String, value: String?) =
            prefs.edit()
                    .putString(key, value)
                    .apply()
    fun putStringSet(key: String, value: Set<String>) =
            prefs.edit()
                    .putStringSet(key, value)
                    .apply()
    fun putInt(key: String, value: Int) =
            prefs.edit()
                    .putInt(key, value)
                    .apply()
    fun putLong(key: String, value: Long) =
            prefs.edit()
                    .putLong(key, value)
                    .apply()
    fun putFloat(key: String, value: Float) =
            prefs.edit()
                    .putFloat(key, value)
                    .apply()
    fun putBoolean(key: String, value: Boolean) =
            prefs.edit()
                    .putBoolean(key, value)
                    .apply()

    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

    fun addCustomBlacklistItem(info: CustomBlacklistInfo) {
        val items = customBlacklistItems

        if (!items.contains(info))
            customBlacklistItems = items.apply { add(info) }
    }

    fun removeCustomBlacklistItem(info: CustomBlacklistInfo) {
        customBlacklistItems = customBlacklistItems.apply { remove(info) }
    }
}