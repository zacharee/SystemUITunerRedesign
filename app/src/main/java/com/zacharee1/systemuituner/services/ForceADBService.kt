package com.zacharee1.systemuituner.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.getNotificationSettingsForChannel
import com.zacharee1.systemuituner.util.writeGlobal

class ForceADBService : Service() {
    companion object {
        private val adbUri = Settings.Global.getUriFor(Settings.Global.ADB_ENABLED)
    }

    private val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            when (uri) {
                adbUri -> {
                    if (!adbEnabled) forceAdbEnabled()
                }
            }
        }
    }

    private val adbEnabled: Boolean
        get() = Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) == 1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val settingsIntent = PendingIntent.getActivity(this, 0, getNotificationSettingsForChannel("force_adb_2"), 0)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("force_adb_2", resources.getString(R.string.force_enable_adb), NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "force_adb_2")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(resources.getString(R.string.force_enable_adb))
                .setContentText(resources.getString(R.string.notif_desc))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(settingsIntent)

        startForeground(1002, notification.build())

        if (!adbEnabled) forceAdbEnabled()
        contentResolver.registerContentObserver(adbUri, true, observer)
    }

    override fun onDestroy() {
        super.onDestroy()

        contentResolver.unregisterContentObserver(observer)
    }

    private fun forceAdbEnabled() {
        writeGlobal(Settings.Global.ADB_ENABLED, 1)
    }
}