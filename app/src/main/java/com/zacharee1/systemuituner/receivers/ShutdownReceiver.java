package com.zacharee1.systemuituner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.zacharee1.systemuituner.activites.SettingsActivity;
import com.zacharee1.systemuituner.misc.SettingsUtils;

public class ShutdownReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Log.e("RIP", "DOWN");

        if ((intent.getAction().equals(Intent.ACTION_SHUTDOWN)
                || intent.getAction().equals("android.intent.action.QUICKBOOT_POWEROFF")
                || intent.getAction().equals(Intent.ACTION_REBOOT)
                || intent.getAction().equals("com.htc.intent.action.QUICKBOOT_POWEROFF")) && sharedPreferences.getBoolean("safe_mode", false)) {
            String currentBL = Settings.Secure.getString(context.getContentResolver(), "icon_blacklist");
            SettingsUtils.writeSecure(context, "icon_blacklist", "");
            SettingsUtils.writeGlobal(context, "icon_blacklist_backup", currentBL);
            SettingsUtils.writeGlobal(context, "system_booted", "0");

            String currentQSVal = Settings.Secure.getString(context.getContentResolver(), "sysui_qs_fancy_anim");
            SettingsUtils.writeGlobal(context, "sysui_qs_fancy_anim_backup", currentQSVal);
            SettingsUtils.writeSecure(context, "sysui_qs_fancy_anim", "1");
        }
    }
}
