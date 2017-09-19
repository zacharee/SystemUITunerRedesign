package com.zacharee1.systemuituner.qstiles;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.ImmersiveHandler;

@TargetApi(24)
public class ImmersiveTile extends TileService
{
    private ContentObserver mObserver;

    @Override
    public void onTileAdded()
    {
        super.onTileAdded();

        getQsTile().setState(Tile.STATE_INACTIVE);
        setTileState();
    }

    @Override
    public void onStartListening()
    {
        super.onStartListening();

        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                if (uri.equals(ImmersiveHandler.POLICY_CONTROL)) {
                    setTileState();
                }
            }
        };

        getContentResolver().registerContentObserver(Settings.Global.CONTENT_URI, true, mObserver);

        setTileState();
    }


    @Override
    public void onClick()
    {
        String toMode = PreferenceManager.getDefaultSharedPreferences(this).getString("immersive_tile_mode", ImmersiveHandler.FULL);

        if (ImmersiveHandler.isInImmersive(this)) toMode = ImmersiveHandler.DISABLED;

        setStateTo(toMode);

        super.onClick();
    }

    private void setStateTo(String toMode) {
        ImmersiveHandler.setMode(this, toMode);
        setTileState();
    }

    private void setTileState() {
        getQsTile().setState(ImmersiveHandler.isInImmersive(this) ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onDestroy()
    {
        try {
            getContentResolver().unregisterContentObserver(mObserver);
        } catch (Exception e) {}

        super.onDestroy();
    }
}
