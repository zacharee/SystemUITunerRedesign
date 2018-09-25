package com.zacharee1.systemuituner.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.settings.SettingsActivity
import com.zacharee1.systemuituner.fragments.MiscFragment
import com.zacharee1.systemuituner.fragments.QSFragment
import com.zacharee1.systemuituner.fragments.TWFragment
import com.zacharee1.systemuituner.util.checkSamsung
import com.zacharee1.systemuituner.util.writeGlobal
import com.zacharee1.systemuituner.util.writeSecure
import com.zacharee1.systemuituner.util.writeSystem

class SafeModeService : Service() {
    private var shutDownReceiver: ShutDownReceiver? = null
    private var themeReceiver: ThemeChangeReceiver? = null
    private var resListener: ResolutionChangeListener? = null

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val observer: ContentObserver = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            if (uri == Settings.Secure.getUriFor("sysui_qqs_count")) {
                restoreQSHeaderCount()
            } else if (uri == Settings.Secure.getUriFor("qs_tile_row")
                    || uri == Settings.Secure.getUriFor("qs_tile_column")) {
                restoreQSRowColCount()
            } else if (uri == Settings.Global.getUriFor("notification_snooze_options")) {
                restoreSnoozeState()
            }
        }
    }

    private lateinit var preferences: SharedPreferences

    private val statusBar: Boolean
        get() = preferences.getBoolean("safe_mode_status_bar", true)
    private val fancyAnim: Boolean
        get() = preferences.getBoolean("safe_mode_fancy_anim", true)
    private val headerCount: Boolean
        get() = preferences.getBoolean("safe_mode_header_count", true)
    private val rowCol: Boolean
        get() = preferences.getBoolean("safe_mode_row_col", true)
    private val snoozeOptions: Boolean
        get() = preferences.getBoolean("safe_mode_snooze_options", true)
    private val hbw: Boolean
        get() = preferences.getBoolean("safe_mode_high_brightness_warning", true)
    private val volumeWarning: Boolean
        get() = preferences.getBoolean("safe_mode_volume_warning", true)

    private val prefsListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
        when (key) {
            "show_safe_mode_notif" -> {
                val on = preferences.getBoolean(key, true)
                if (!on) {
                    stopForeground(true)
                } else {
                    startInForeground()
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.registerOnSharedPreferenceChangeListener(prefsListener)

        startInForeground()
        restoreStateOnStartup()
        restoreQSHeaderCount()
        restoreHBWState()
        restoreQSRowColCount()
        setUpReceivers()
        setUpContentObserver()
        restoreSnoozeState()
        restoreVolumeWarning()
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            unregisterReceiver(themeReceiver)
        } catch (e: Exception) {}

        try {
            unregisterReceiver(shutDownReceiver)
        } catch (e: Exception) {}

        try {
            contentResolver.unregisterContentObserver(resListener)
        } catch (e: Exception) {}

        try {
            contentResolver.unregisterContentObserver(observer)
        } catch (e: Exception) {}

        try {
            preferences.unregisterOnSharedPreferenceChangeListener(prefsListener)
        } catch (e: Exception) {}
    }

    private fun startInForeground() {
        if (preferences.getBoolean("show_safe_mode_notif", true)) {
            val settingsIntent = PendingIntent.getActivity(this, 0, Intent(this, SettingsActivity::class.java), 0)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel("systemuituner", resources.getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW)
                manager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(this, "systemuituner")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(resources.getString(R.string.notif_title))
                    .setContentText(resources.getString(R.string.notif_desc))
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setContentIntent(settingsIntent)

            startForeground(1001, notification.build())
        }
    }

    private fun setUpReceivers() {
        shutDownReceiver = ShutDownReceiver()
        themeReceiver = ThemeChangeReceiver()
        resListener = ResolutionChangeListener(handler)
    }

    private fun restoreStateOnStartup() {
        restoreBlacklist()
        restoreFancyAnim()
    }

    private fun restoreHBWState() {
        if (hbw && checkSamsung()) {
            val restoreState = preferences.getBoolean(TWFragment.HIGH_BRIGHTNESS_WARNING, true)
            writeSystem(TWFragment.HIGH_BRIGHTNESS_WARNING, if (restoreState) 0 else 1, false)
        }
    }

    private fun restoreBlacklist() {
        if (statusBar) {
            val blacklist = Settings.Secure.getString(contentResolver, "icon_blacklist")

            if (blacklist == null || blacklist.isEmpty()) {
                val blacklistBackup = Settings.Global.getString(contentResolver, "icon_blacklist_backup")

                if (blacklistBackup != null && !blacklistBackup.isEmpty()) {
                    writeSecure("icon_blacklist", blacklistBackup)
                }
            }
        }
    }

    private fun restoreFancyAnim() {
        if (fancyAnim) {
            val fancyAnim = preferences.getBoolean(QSFragment.FANCY_ANIM, true)

            writeSecure(QSFragment.FANCY_ANIM, if (fancyAnim) 1 else 0)
        }
    }

    private fun restoreVolumeWarning() {
        if (volumeWarning) {
            val warn = preferences.getBoolean(MiscFragment.AUDIO_SAFE, true)

            writeGlobal(MiscFragment.AUDIO_SAFE, if (warn) 3 else 2)
        }
    }

    private fun restoreQSHeaderCount() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && headerCount) {
            val count = preferences.getInt("qs_header_count", -1)
            if (count != -1) writeSecure("sysui_qqs_count", count)
        }
    }

    private fun saveQSHeaderCount() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && headerCount) {
            val count = Settings.Secure.getInt(contentResolver, "sysui_qqs_count", -1)
            if (count != -1) preferences.edit().putInt("qs_header_count", count).apply()
        }
    }

    private fun saveQSRowColCount() {
        if (rowCol) {
            val row = Settings.Secure.getInt(contentResolver, "qs_tile_row", -1)
            val col = Settings.Secure.getInt(contentResolver, "qs_tile_column", -1)
            if (row != -1) preferences.edit().putInt("qs_tile_row", row).apply()
            if (col != -1) preferences.edit().putInt("qs_tile_column", col).apply()
        }
    }

    private fun restoreQSRowColCount() {
        if (rowCol) {
            val row = preferences.getInt("qs_tile_row", -1)
            val col = preferences.getInt("qs_tile_column", -1)

            if (row != -1) writeSecure("qs_tile_row", row)
            if (col != -1) writeSecure("qs_tile_column", col)
        }
    }

    private fun restoreSnoozeState() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && snoozeOptions) {
            val saved = preferences.getString("notification_snooze_options", "")

            if (saved?.isEmpty() == false) {
                writeGlobal("notification_snooze_options", saved)
            }
        }
    }

    private fun saveSnoozeState() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && snoozeOptions) {
            val set = Settings.Global.getString(contentResolver, "notification_snooze_options")
            if (set != null && !set.isEmpty()) preferences.edit().putString("notification_snooze_options", set).apply()
        }
    }

    private fun saveFancyAnim() {
        if (fancyAnim) {
            val anim = Settings.Secure.getString(contentResolver, "sysui_qs_fancy_anim")
            writeGlobal("sysui_qs_fancy_anim_backup", anim)
            writeSecure("sysui_qs_fancy_anim", null)
        }
    }

    private fun setUpContentObserver() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            contentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, observer)
            contentResolver.registerContentObserver(Settings.Secure.CONTENT_URI, true, observer)
        }
    }

    private fun resetBlacklist(restore: Boolean) {
        if (statusBar) {
            val blacklist = Settings.Secure.getString(contentResolver, "icon_blacklist")

            writeGlobal("icon_blacklist_backup", blacklist)
            writeSecure("icon_blacklist", null)

            if (restore) {
                handler.postDelayed({ writeSecure("icon_blacklist", blacklist) }, 400)
            }
        }
    }

    inner class ShutDownReceiver : BroadcastReceiver() {
        init {
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SHUTDOWN)
            filter.addAction(Intent.ACTION_REBOOT)
            filter.addAction("android.intent.action.QUICKBOOT_POWEROFF")
            filter.addAction("com.htc.intent.action.QUICKBOOT_POWEROFF")

            registerReceiver(this, filter)
        }

        override fun onReceive(context: Context, intent: Intent) {
            resetBlacklist(false)
            saveQSHeaderCount()
            saveQSRowColCount()
            saveSnoozeState()
            saveFancyAnim()
        }
    }

    inner class ThemeChangeReceiver : BroadcastReceiver() {
        init {
            val filter = IntentFilter("com.samsung.android.theme.themecenter.THEME_APPLY")

            registerReceiver(this, filter)
        }

        override fun onReceive(context: Context, intent: Intent) {
            resetBlacklist(true)
        }
    }

    inner class ResolutionChangeListener(handler: Handler?) : ContentObserver(handler) {
        init {

            contentResolver.registerContentObserver(Settings.Secure.CONTENT_URI, true, this)
        }

        override fun onChange(selfChange: Boolean, uri: Uri) {
            val twRes = Settings.Secure.getUriFor("default_display_size_forced")
            val res = Settings.Secure.getUriFor("display_size_forced")

            if (uri == twRes || uri == res) {
                resetBlacklist(true)
            }
        }
    }
}
