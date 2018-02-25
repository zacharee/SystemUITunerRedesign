package com.zacharee1.systemuituner.services

import android.app.*
import android.content.*
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.settings.SettingsActivity
import com.zacharee1.systemuituner.util.SettingsUtils

class SafeModeService : Service() {
    private var mShutDownReceiver: ShutDownReceiver? = null
    private var mThemeReceiver: ThemeChangeReceiver? = null

    private var mResListener: ResolutionChangeListener? = null

    private var mHandler: Handler? = null
    private var observer: ContentObserver? = null
    private var preferences: SharedPreferences? = null

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
        mHandler = Handler(Looper.getMainLooper())
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences?.registerOnSharedPreferenceChangeListener(prefsListener)

        startInForeground()
        restoreStateOnStartup()
        restoreQSHeaderCount()
        restoreQSRowColCount()
        setUpReceivers()
        setUpContentObserver()
        restoreSnoozeState()
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            unregisterReceiver(mThemeReceiver)
        } catch (e: Exception) {}

        try {
            unregisterReceiver(mShutDownReceiver)
        } catch (e: Exception) {}

        try {
            contentResolver.unregisterContentObserver(mResListener!!)
        } catch (e: Exception) {}

        try {
            contentResolver.unregisterContentObserver(observer!!)
        } catch (e: Exception) {}

        try {
            preferences?.unregisterOnSharedPreferenceChangeListener(prefsListener)
        } catch (e: Exception) {}
    }

    private fun startInForeground() {
        val settingsIntent = PendingIntent.getActivity(this, 0, Intent(this, SettingsActivity::class.java), 0)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(resources.getString(R.string.notif_title))
                .setContentText(resources.getString(R.string.notif_desc))
                .setPriority(Notification.PRIORITY_MIN)
                .setContentIntent(settingsIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("zacharee1", "SystemUI Tuner", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
            notification.setChannelId("zacharee1")
        }

        startForeground(1001, notification.build())
    }

    private fun setUpReceivers() {
        mShutDownReceiver = ShutDownReceiver()
        mThemeReceiver = ThemeChangeReceiver()
        mResListener = ResolutionChangeListener(mHandler)
    }

    private fun restoreStateOnStartup() {
        val blacklist = Settings.Secure.getString(contentResolver, "icon_blacklist")

        if (blacklist == null || blacklist.isEmpty()) {
            val blacklistBackup = Settings.Global.getString(contentResolver, "icon_blacklist_backup")

            if (blacklistBackup != null && !blacklistBackup.isEmpty()) {
                SettingsUtils.writeSecure(this, "icon_blacklist", blacklistBackup)
            }
        }

        val qsAnimState = Settings.Secure.getString(contentResolver, "sysui_qs_fancy_anim")

        if (qsAnimState == null || qsAnimState.isEmpty() || qsAnimState == "1") {
            val backupState = Settings.Global.getString(contentResolver, "sysui_qs_fancy_anim_backup")

            if (backupState != null && !backupState.isEmpty()) {
                SettingsUtils.writeSecure(this, "sysui_qs_fancy_anim", backupState)
            }
        }

        SettingsUtils.writeGlobal(this, "system_booted", "1")
    }

    private fun restoreQSHeaderCount() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val count = preferences!!.getInt("qs_header_count", -1)
            if (count != -1) SettingsUtils.writeSecure(this, "sysui_qqs_count", count.toString())
        }
    }

    private fun saveQSHeaderCount() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val count = Settings.Secure.getInt(contentResolver, "sysui_qqs_count", -1)
            if (count != -1) preferences!!.edit().putInt("qs_header_count", count).apply()
        }
    }

    private fun saveQSRowColCount() {
        val row = Settings.Secure.getInt(contentResolver, "qs_tile_row", -1)
        val col = Settings.Secure.getInt(contentResolver, "qs_tile_column", -1)
        if (row != -1) preferences!!.edit().putInt("qs_tile_row", row).apply()
        if (col != -1) preferences!!.edit().putInt("qs_tile_column", col).apply()
    }

    private fun restoreQSRowColCount() {
        val row = preferences!!.getInt("qs_tile_row", -1)
        val col = preferences!!.getInt("qs_tile_column", -1)

        if (row != -1) SettingsUtils.writeSecure(this, "qs_tile_row", row.toString() + "")
        if (col != -1) SettingsUtils.writeSecure(this, "qs_tile_column", col.toString() + "")
    }

    private fun restoreSnoozeState() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val saved = preferences!!.getString("notification_snooze_options", "")

            if (!saved!!.isEmpty()) {
                SettingsUtils.writeGlobal(this, "notification_snooze_options", saved)
            }
        }
    }

    private fun saveSnoozeState() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val set = Settings.Global.getString(contentResolver, "notification_snooze_options")
            if (set != null && !set.isEmpty()) preferences!!.edit().putString("notification_snooze_options", set).apply()
        }
    }

    private fun setUpContentObserver() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean, uri: Uri) {
                    if (uri == Settings.Secure.getUriFor("sysui_qqs_count")) {
                        restoreQSHeaderCount()
                    } else if (uri == Settings.Secure.getUriFor("qs_tile_row") || uri == Settings.Secure.getUriFor("qs_tile_column")) {
                        restoreQSRowColCount()
                    } else if (uri == Settings.Global.getUriFor("notification_snooze_options")) {
                        restoreSnoozeState()
                    }
                }
            }

            contentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, observer!!)
        }
    }

    private fun resetBlacklist(restore: Boolean) {
        val blacklist = Settings.Secure.getString(contentResolver, "icon_blacklist")

        SettingsUtils.writeGlobal(this, "icon_blacklist_backup", blacklist)
        SettingsUtils.writeSecure(this, "icon_blacklist", "")

        if (restore) {
            mHandler!!.postDelayed({ SettingsUtils.writeSecure(this@SafeModeService, "icon_blacklist", blacklist) }, 400)
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
        }
    }

    inner class ThemeChangeReceiver : BroadcastReceiver() {
        init {
            val filter = IntentFilter("broadcast com.samsung.android.theme.themecenter.THEME_APPLY")

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
