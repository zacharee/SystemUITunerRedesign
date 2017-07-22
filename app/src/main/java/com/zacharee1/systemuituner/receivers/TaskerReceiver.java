package com.zacharee1.systemuituner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zacharee1.systemuituner.misc.MiscStrings;
import com.zacharee1.systemuituner.misc.SettingsUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class TaskerReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.e("DATA", "RECEIVED");
        Log.e("DATA", intent.getDataString());

        if ((intent.getAction().equals(MiscStrings.ACTION_SETTINGS_GLOBAL) ||
                intent.getAction().equals(MiscStrings.ACTION_SETTINGS_SECURE) ||
                intent.getAction().equals(MiscStrings.ACTION_SETTINGS_SYSTEM)) &&
                PreferenceManager.getDefaultSharedPreferences(context).getBoolean("tasker_support_enabled", false)) {

            String dataString = intent.getDataString();

            //expecting dataString with format "SETTING:key/value"
            String[] nameVal = dataString.split("[:]");
            if (nameVal[1] == null) nameVal[1] = "";
            String keyVal = nameVal[1];

            ArrayList<String> keyValPair = new ArrayList<>(Arrays.asList(keyVal.split("[/]")));
            if (keyValPair.size() < 2) keyValPair.set(1, "");

            switch (intent.getAction()) {
                case MiscStrings.ACTION_SETTINGS_GLOBAL:
                    SettingsUtils.writeGlobal(context, keyValPair.get(0), keyValPair.get(1));
                    break;
                case MiscStrings.ACTION_SETTINGS_SECURE:
                    SettingsUtils.writeSecure(context, keyValPair.get(0), keyValPair.get(1));
                    break;
                case MiscStrings.ACTION_SETTINGS_SYSTEM:
                    SettingsUtils.writeSystem(context, keyValPair.get(0), keyValPair.get(1));
                    break;
            }
        }
    }
}
