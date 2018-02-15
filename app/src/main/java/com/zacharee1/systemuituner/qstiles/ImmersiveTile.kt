package com.zacharee1.systemuituner.qstiles

import android.annotation.TargetApi
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

import com.zacharee1.systemuituner.handlers.ImmersiveHandler

@TargetApi(24)
class ImmersiveTile : TileService() {
    private var mObserver: ContentObserver? = null

    override fun onStartListening() {
        super.onStartListening()

        mObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri) {
                if (uri == ImmersiveHandler.POLICY_CONTROL) {
                    setTileState()
                }
            }
        }

        contentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, mObserver!!)

        setTileState()
    }


    override fun onClick() {
        var toMode = PreferenceManager.getDefaultSharedPreferences(this).getString("immersive_tile_mode", ImmersiveHandler.FULL)

        if (ImmersiveHandler.isInImmersive(this)) toMode = ImmersiveHandler.DISABLED

        setStateTo(toMode)

        super.onClick()
    }

    private fun setStateTo(toMode: String) {
        ImmersiveHandler.setMode(this, toMode)
        setTileState()
    }

    private fun setTileState() {
        qsTile.state = if (ImmersiveHandler.isInImmersive(this)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onDestroy() {
        try {
            contentResolver.unregisterContentObserver(mObserver!!)
        } catch (e: Exception) {
        }

        super.onDestroy()
    }
}
