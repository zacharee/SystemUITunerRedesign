package com.zacharee1.systemuituner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;

import com.zacharee1.systemuituner.services.SafeModeService;

public class PackageReplaceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)
                && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("safe_mode", false)) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) context.startService(new Intent(context, SafeModeService.class));
        }
    }
}
