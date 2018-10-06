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
import android.view.Surface
import android.view.WindowManager
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.settings.SettingsActivity
import com.zacharee1.systemuituner.fragments.MiscFragment
import com.zacharee1.systemuituner.fragments.QSFragment
import com.zacharee1.systemuituner.fragments.StatbarFragment
import com.zacharee1.systemuituner.fragments.StatbarFragment.Companion.ICON_BLACKLIST
import com.zacharee1.systemuituner.fragments.StatbarFragment.Companion.ICON_BLACKLIST_BACKUP
import com.zacharee1.systemuituner.fragments.TWFragment
import com.zacharee1.systemuituner.util.*

class SafeModeService : Service() {
    companion object {
        const val NOTIFICATION_SNOOZE_OPTIONS = "notification_snooze_options"

        const val SAFE_MODE_STATUS_BAR = "safe_mode_status_bar"
        const val SAFE_MODE_FANCY_ANIM = "safe_mode_fancy_anim"
        const val SAFE_MODE_HEADER_COUNT = "safe_mode_header_count"
        const val SAFE_MODE_ROW_COL = "safe_mode_row_col"
        const val SAFE_MODE_SNOOZE_OPTIONS = "safe_mode_snooze_options"
        const val SAFE_MODE_HIGH_BRIGHTNESS_WARNING = "safe_mode_high_brightness_warning"
        const val SAFE_MODE_VOLUME_WARNING = "safe_mode_volume_warning"
        const val SAFE_MODE_CALL_RECORDING = "safe_mode_call_recording"
        const val SAFE_MODE_NOTIF = "show_safe_mode_notif"
    }

    private var shutDownReceiver: ShutDownReceiver? = null
    private var themeReceiver: ThemeChangeReceiver? = null
    private var resListener: ResolutionChangeListener? = null

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val observer: ContentObserver = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            if (uri == Settings.Secure.getUriFor(QSFragment.QQS_COUNT)) {
                restoreQSHeaderCount()
            } else if (uri == Settings.Secure.getUriFor(TWFragment.TILE_ROW)
                    || uri == Settings.Secure.getUriFor(TWFragment.TILE_COLUMN)) {
                restoreQSRowColCount()
            } else if (uri == Settings.Global.getUriFor(NOTIFICATION_SNOOZE_OPTIONS)) {
                restoreSnoozeState()
            }
        }
    }

    private lateinit var preferences: SharedPreferences

    private val statusBar: Boolean
        get() = preferences.getBoolean(SAFE_MODE_STATUS_BAR, true)
    private val fancyAnim: Boolean
        get() = preferences.getBoolean(SAFE_MODE_FANCY_ANIM, true)
    private val headerCount: Boolean
        get() = preferences.getBoolean(SAFE_MODE_HEADER_COUNT, true)
    private val rowCol: Boolean
        get() = preferences.getBoolean(SAFE_MODE_ROW_COL, true)
    private val snoozeOptions: Boolean
        get() = preferences.getBoolean(SAFE_MODE_SNOOZE_OPTIONS, true)
    private val hbw: Boolean
        get() = preferences.getBoolean(SAFE_MODE_HIGH_BRIGHTNESS_WARNING, true)
    private val volumeWarning: Boolean
        get() = preferences.getBoolean(SAFE_MODE_VOLUME_WARNING, true)
    private val callRecorder: Boolean
        get() = preferences.getBoolean(SAFE_MODE_CALL_RECORDING, true)

    private val prefsListener: SharedPreferences.OnSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
        when (key) {
            SAFE_MODE_NOTIF -> {
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
        restoreCallRecorder()

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
        if (preferences.getBoolean(SAFE_MODE_NOTIF, true)) {
            val settingsIntent = PendingIntent.getActivity(this, 0, Intent(this, SettingsActivity::class.java), 0)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel("safe_mode", resources.getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW)
                manager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(this, "safe_mode")
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
            val blacklist = Settings.Secure.getString(contentResolver, ICON_BLACKLIST)

            if (blacklist == null || blacklist.isEmpty()) {
                val blacklistBackup = Settings.Global.getString(contentResolver, ICON_BLACKLIST_BACKUP)

                if (blacklistBackup != null && !blacklistBackup.isEmpty()) {
                    writeSecure(ICON_BLACKLIST, blacklistBackup)
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

    private fun restoreCallRecorder() {
        if (callRecorder && checkOnePlusWithCallRecording()) {
            val callRecorder = preferences.getBoolean(MiscFragment.ONE_PLUS_CALL_RECORDER, true)

            writeGlobal(MiscFragment.ONE_PLUS_CALL_RECORDER, if (callRecorder) 1 else 0)
        }
    }

    private fun restoreQSHeaderCount() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && headerCount) {
            val count = preferences.getInt(QSFragment.QQS_COUNT, -1)
            if (count != -1) writeSecure(QSFragment.QQS_COUNT, count)
        }
    }

    private fun restoreQSRowColCount() {
        val rotation = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
        if (rowCol) {
            if (rotation != Surface.ROTATION_180 && rotation != Surface.ROTATION_270) {
                val row = preferences.getInt(TWFragment.TILE_ROW, -1)
                val col = preferences.getInt(TWFragment.TILE_COLUMN, -1)

                if (row != -1) writeSecure(TWFragment.TILE_ROW, row)
                if (col != -1) writeSecure(TWFragment.TILE_COLUMN, col)
            } else {
                val row = preferences.getInt(TWFragment.TILE_ROW_LANDSCAPE, -1)
                val col = preferences.getInt(TWFragment.TILE_COLUMN_LANDSCAPE, -1)

                if (row != -1) writeSecure(TWFragment.TILE_ROW, row)
                if (col != -1) writeSecure(TWFragment.TILE_COLUMN, col)
            }
        }
    }

    private fun restoreSnoozeState() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && snoozeOptions) {
            val saved = preferences.getString(NOTIFICATION_SNOOZE_OPTIONS, "")

            if (saved?.isEmpty() == false) {
                writeGlobal(NOTIFICATION_SNOOZE_OPTIONS, saved)
            }
        }
    }

    private fun saveSnoozeState() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && snoozeOptions) {
            val set = Settings.Global.getString(contentResolver, NOTIFICATION_SNOOZE_OPTIONS)
            if (set != null && !set.isEmpty()) preferences.edit().putString(NOTIFICATION_SNOOZE_OPTIONS, set).apply()
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
            val blacklist = Settings.Secure.getString(contentResolver, StatbarFragment.ICON_BLACKLIST)

            writeGlobal(StatbarFragment.ICON_BLACKLIST_BACKUP, blacklist)
            writeSecure(StatbarFragment.ICON_BLACKLIST, null)

            if (restore) {
                handler.postDelayed({ writeSecure(StatbarFragment.ICON_BLACKLIST, blacklist) }, 400)
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
            saveSnoozeState()
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
