package com.zacharee1.systemuituner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.zacharee1.systemuituner.utils.SettingsUtils;

public class ShutdownReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN) && sharedPreferences.getBoolean("safe_mode", false)) {
            String currentBL = Settings.Secure.getString(context.getContentResolver(), "icon_blacklist");
            SettingsUtils.writeGlobal(context, "icon_blacklist_backup", currentBL);
            SettingsUtils.writeGlobal(context, "system_booted", "0");
        }
    }
}
