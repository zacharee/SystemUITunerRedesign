package com.zacharee1.systemuituner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

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
//            String backupBL = Settings.Global.getString(context.getContentResolver(), "icon_blacklist_backup");
//            SettingsUtils.writeSecure(context, "icon_blacklist", backupBL);
//            SettingsUtils.writeGlobal(context, "system_booted", "1");
//
//            String backupQSVal = Settings.Global.getString(context.getContentResolver(), "sysui_qs_fancy_anim_backup");
//            SettingsUtils.writeSecure(context, "sysui_qs_fancy_anim", backupQSVal);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                context.startService(new Intent(context, SafeModeService.class));
            }

            Log.e("BOOTED", "BOOTED");

//            JobScheduler jobScheduler = (JobScheduler)
//                    context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//
//            JobInfo.Builder builder = new JobInfo.Builder(1,
//                    new ComponentName( context.getPackageName(),
//                            ShutdownService.class.getName()));
//
//            jobScheduler.schedule(builder.build());
        }
    }
}
