package com.zacharee1.systemuituner.qstiles

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.zacharee1.systemuituner.util.writeSecure

@TargetApi(24)
class NightModeTile : TileService() {
    @SuppressLint("PrivateApi")
    override fun onStartListening() {
        val isActive: Boolean = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
            Settings.Secure.getInt(contentResolver, "twilight_mode", 0) != 0
        else
            Settings.Secure.getInt(contentResolver, "night_display_activated", 0) == 1

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            try {
                val InternalBool = Class.forName("com.android.internal.R\$bool")

                val nightDisplayAvailable = InternalBool.getField("config_nightDisplayAvailable")
                val id = nightDisplayAvailable.getInt(null)

                if (!Resources.getSystem().getBoolean(id)) {
                    qsTile?.state = Tile.STATE_UNAVAILABLE
                    qsTile?.updateTile()
                } else {
                    qsTile?.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                    qsTile?.updateTile()
                }
            } catch (e: Exception) {
                e.printStackTrace()

                qsTile?.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                qsTile?.updateTile()
            }

        } else {
            qsTile?.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile?.updateTile()
        }
    }

    override fun onClick() {
        val isActive: Boolean = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
            Settings.Secure.getInt(contentResolver, "twilight_mode", 0) != 0
        else
            Settings.Secure.getInt(contentResolver, "night_display_activated", 0) == 1

        if (isActive) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
                writeSecure("twilight_mode", 0)
            else
                writeSecure("night_display_activated", 0)
        } else {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N)
                writeSecure("twilight_mode", 1)
            else
                writeSecure("night_display_activated", 1)
        }

        onStartListening()

        super.onClick()
    }
}
