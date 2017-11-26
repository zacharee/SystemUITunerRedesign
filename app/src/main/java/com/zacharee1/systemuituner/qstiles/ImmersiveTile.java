package com.zacharee1.systemuituner.qstiles;

import android.annotation.TargetApi;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.zacharee1.systemuituner.handlers.ImmersiveHandler;

@TargetApi(24)
public class ImmersiveTile extends TileService
{
    private ContentObserver mObserver;

    @Override
    public void onTileAdded()
    {
        super.onTileAdded();
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
