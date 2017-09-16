package com.zacharee1.systemuituner.qstiles;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.BatteryManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.DemoHandler;

@TargetApi(24)
public class DemoTile extends TileService
{
    private BroadcastReceiver mReceiver;
    private DemoHandler mDemoHandler;

    @Override
    public void onTileAdded()
    {
        super.onTileAdded();

        getQsTile().setState(Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onStartListening()
    {
        super.onStartListening();

        mDemoHandler = new DemoHandler(this);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("com.android.systemui.demo")) {
                    String mode = intent.getStringExtra("command");

                    if (mode != null) {
                        setState();
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter("com.android.systemui.demo");

        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {}

        registerReceiver(mReceiver, filter);

        setState();
    }

    private void setState() {
        if (mDemoHandler.isAllowed()) {
            getQsTile().setState(mDemoHandler.isEnabled() ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        } else {
            getQsTile().setState(Tile.STATE_UNAVAILABLE);
        }
        getQsTile().updateTile();
    }

    @Override
    public void onClick()
    {
        if (mDemoHandler.isEnabled()) {
            mDemoHandler.hideDemo();
        } else {
            mDemoHandler.showDemo();
        }

        setState();

        super.onClick();
    }

    @Override
    public void onDestroy()
    {
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {}

        super.onDestroy();
    }
}
