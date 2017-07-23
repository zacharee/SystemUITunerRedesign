package com.zacharee1.systemuituner.qstiles;

import android.annotation.TargetApi;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.zacharee1.systemuituner.misc.SettingsUtils;

@TargetApi(24)
public class NightModeTile extends TileService
{
    @Override
    public void onStartListening()
    {
        final Tile nightMode = getQsTile();
        boolean isActive;

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) isActive = Settings.Secure.getInt(getContentResolver(), "twilight_mode", 0) != 0;
        else isActive = Settings.Secure.getInt(getContentResolver(), "night_display_activated", 0) == 1;

        nightMode.setState(isActive ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        nightMode.updateTile();
    }

    @Override
    public void onClick()
    {
        boolean isActive;

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) isActive = Settings.Secure.getInt(getContentResolver(), "twilight_mode", 0) != 0;
        else isActive = Settings.Secure.getInt(getContentResolver(), "night_display_activated", 0) == 1;

        if (isActive) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) SettingsUtils.writeSecure(this, "twilight_mode", "0");
            else SettingsUtils.writeSecure(this, "night_display_activated", "0");
        } else {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) SettingsUtils.writeSecure(this, "twilight_mode", "1");
            else SettingsUtils.writeSecure(this, "night_display_activated", "1");
        }

        onStartListening();

        super.onClick();
    }

}
