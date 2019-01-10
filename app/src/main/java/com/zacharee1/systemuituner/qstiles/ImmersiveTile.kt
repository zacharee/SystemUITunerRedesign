package com.zacharee1.systemuituner.qstiles

import android.annotation.TargetApi
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.zacharee1.systemuituner.handlers.ImmersiveHandler
import com.zacharee1.systemuituner.util.prefs

@TargetApi(24)
class ImmersiveTile : TileService() {
    private val observer by lazy {
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri) {
                if (uri == ImmersiveHandler.POLICY_CONTROL) {
                    setTileState()
                }
            }
        }
    }

    override fun onStartListening() {
        super.onStartListening()

        contentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, observer!!)

        setTileState()
    }


    override fun onClick() {
        var toMode = prefs.immersiveTileMode!!

        if (ImmersiveHandler.isInImmersive(this))
            toMode = ImmersiveHandler.DISABLED

        setStateTo(toMode)

        super.onClick()
    }

    private fun setStateTo(toMode: String) {
        ImmersiveHandler.setMode(this, toMode)
        setTileState()
    }

    private fun setTileState() {
        qsTile?.state = if (ImmersiveHandler.isInImmersive(this)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile?.updateTile()
    }

    override fun onDestroy() {
        try {
            contentResolver.unregisterContentObserver(observer!!)
        } catch (e: Exception) {
        }

        super.onDestroy()
    }
}
