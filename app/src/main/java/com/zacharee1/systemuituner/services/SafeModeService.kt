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
import android.provider.Settings
import android.view.Surface
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.settings.SettingsActivity
import com.zacharee1.systemuituner.fragments.StatbarFragment
import com.zacharee1.systemuituner.fragments.StatbarFragment.Companion.ICON_BLACKLIST
import com.zacharee1.systemuituner.fragments.StatbarFragment.Companion.ICON_BLACKLIST_BACKUP
import com.zacharee1.systemuituner.util.*
import com.zacharee1.systemuituner.util.PrefManager.Companion.AUDIO_SAFE
import com.zacharee1.systemuituner.util.PrefManager.Companion.HIGH_BRIGHTNESS_WARNING
import com.zacharee1.systemuituner.util.PrefManager.Companion.NOTIFICATION_SNOOZE_OPTIONS
import com.zacharee1.systemuituner.util.PrefManager.Companion.QQS_COUNT
import com.zacharee1.systemuituner.util.PrefManager.Companion.QS_FANCY_ANIM
import com.zacharee1.systemuituner.util.PrefManager.Companion.SAFE_MODE_NOTIF
import com.zacharee1.systemuituner.util.PrefManager.Companion.TILE_COLUMN
import com.zacharee1.systemuituner.util.PrefManager.Companion.TILE_ROW

class SafeModeService : Service() {
    private var shutDownReceiver: ShutDownReceiver? = null
    private var themeReceiver: ThemeChangeReceiver? = null
    private var resListener: ResolutionChangeListener? = null

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val observer: ContentObserver = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            if (uri == Settings.Secure.getUriFor(QQS_COUNT)) {
                restoreQSHeaderCount()
            } else if (uri == Settings.Secure.getUriFor(TILE_ROW)
                    || uri == Settings.Secure.getUriFor(TILE_COLUMN)) {
                restoreQSRowColCount()
            } else if (uri == Settings.Global.getUriFor(NOTIFICATION_SNOOZE_OPTIONS)) {
                restoreSnoozeState()
            }
        }
    }

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
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)

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
            prefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
        } catch (e: Exception) {}
    }

    private fun startInForeground() {
        if (prefs.safeModeNotif) {
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
        if (prefs.safeModeHbw && checkSamsung()) {
            writeSystem(HIGH_BRIGHTNESS_WARNING, if (prefs.highBrightnessWarning) 0 else 1, false)
        }
    }

    private fun restoreBlacklist() {
        if (prefs.safeModeStatusBar) {
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
        if (prefs.safeModeFancyAnim) {
            writeSecure(QS_FANCY_ANIM, if (prefs.qsFancyAnim) 1 else 0)
        }
    }

    private fun restoreVolumeWarning() {
        if (prefs.safeModeVolumeWarning) {
            writeGlobal(AUDIO_SAFE, if (prefs.audioSafe) 3 else 2)
        }
    }

    private fun restoreQSHeaderCount() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && prefs.safeModeHeaderCount) {
            val count = prefs.qqsCount
            if (count != -1) writeSecure(QQS_COUNT, count)
        }
    }

    private fun restoreQSRowColCount() {
        val rotation = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
        if (prefs.safeModeRowCol) {
            if (rotation != Surface.ROTATION_180 && rotation != Surface.ROTATION_270) {
                val row = prefs.tileRow
                val col = prefs.tileColumn

                if (row != -1) writeSecure(PrefManager.TILE_ROW, row)
                if (col != -1) writeSecure(PrefManager.TILE_COLUMN, col)
            } else {
                val row = prefs.tileRowLandscape
                val col = prefs.tileColumnLandscape

                if (row != -1) writeSecure(PrefManager.TILE_ROW, row)
                if (col != -1) writeSecure(PrefManager.TILE_COLUMN, col)
            }
        }
    }

    private fun restoreSnoozeState() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && prefs.safeModeSnoozeOptions) {
            val saved = prefs.notificationSnoozeOptions

            if (saved?.isEmpty() == false) {
                writeGlobal(NOTIFICATION_SNOOZE_OPTIONS, saved)
            }
        }
    }

    private fun saveSnoozeState() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && prefs.safeModeSnoozeOptions) {
            val set = Settings.Global.getString(contentResolver, NOTIFICATION_SNOOZE_OPTIONS)
            if (set != null && !set.isEmpty()) prefs.notificationSnoozeOptions = set
        }
    }

    private fun setUpContentObserver() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            contentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, observer)
            contentResolver.registerContentObserver(Settings.Secure.CONTENT_URI, true, observer)
        }
    }

    private fun resetBlacklist(restore: Boolean) {
        if (prefs.safeModeStatusBar) {
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
