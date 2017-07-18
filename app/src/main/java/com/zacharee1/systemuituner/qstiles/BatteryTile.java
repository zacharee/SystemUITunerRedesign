package com.zacharee1.systemuituner.qstiles;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.BatteryManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.zacharee1.systemuituner.R;

@TargetApi(24)
public class BatteryTile extends TileService
{
    private BroadcastReceiver mReceiver;

    @Override
    public void onTileAdded()
    {
        super.onTileAdded();

        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onStartListening()
    {
        super.onStartListening();

        mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                setLevel(context, intent);
            }
        };

        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onClick()
    {
        Intent intentBatteryUsage = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
        startActivityAndCollapse(intentBatteryUsage);

        super.onClick();
    }

    @Override
    public void onDestroy()
    {
        try {
            unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    private void setLevel(Context context, Intent intent) {
        Tile batteryTile = getQsTile();
        int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean batteryCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                batteryStatus == BatteryManager.BATTERY_STATUS_FULL;
        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int resId;

        if (batteryLevel >= 95) {
            resId = batteryCharging ? R.drawable.ic_battery_charging_full_black_24dp : R.drawable.ic_battery_full_black_24dp;
        } else if (batteryLevel >= 85) {
            resId = batteryCharging ? R.drawable.ic_battery_charging_90_black_24dp : R.drawable.ic_battery_90_black_24dp;
        } else if (batteryLevel >= 70) {
            resId = batteryCharging ? R.drawable.ic_battery_charging_80_black_24dp : R.drawable.ic_battery_80_black_24dp;
        } else if (batteryLevel >= 55) {
            resId = batteryCharging ? R.drawable.ic_battery_charging_60_black_24dp : R.drawable.ic_battery_60_black_24dp;
        } else if (batteryLevel >= 40) {
            resId = batteryCharging ? R.drawable.ic_battery_charging_50_black_24dp : R.drawable.ic_battery_50_black_24dp;
        } else if (batteryLevel >= 25) {
            resId = batteryCharging ? R.drawable.ic_battery_charging_30_black_24dp : R.drawable.ic_battery_30_black_24dp;
        } else if (batteryLevel >= 15) {
            resId = batteryCharging ? R.drawable.ic_battery_charging_20_black_24dp : R.drawable.ic_battery_20_black_24dp;
        } else {
            resId = batteryCharging ? R.drawable.ic_battery_charging_20_black_24dp : R.drawable.ic_battery_alert_black_24dp;
        }

        batteryTile.setIcon(Icon.createWithResource(this, resId));
        batteryTile.setLabel(batteryLevel + "%");
        batteryTile.updateTile();
    }
}
