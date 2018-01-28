package com.zacharee1.systemuituner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.zacharee1.systemuituner.services.SafeModeService;

public class BootReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (action != null &&
                sharedPreferences.getBoolean("safe_mode", false) && (
                action.equals(Intent.ACTION_BOOT_COMPLETED) ||
                action.equals(Intent.ACTION_REBOOT) ||
                action.equals("android.intent.action.QUICKBOOT_POWERON") ||
                action.equals("com.htc.intent.action.QUICKBOOT_POWERON")
        )) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                context.startService(new Intent(context, SafeModeService.class));
            } else {
                context.startForegroundService(new Intent(context, SafeModeService.class));
            }
        }
    }
}
