package com.zacharee1.systemuituner.misc;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.zacharee1.systemuituner.activites.SettingWriteFailed;

import static com.zacharee1.systemuituner.misc.SuUtils.testSudo;
import static com.zacharee1.systemuituner.misc.SuUtils.sudo;

/**
 * Created by Zacha on 7/17/2017.
 */

public class SettingsUtils
{
    public static void writeGlobal(Context context, String key, String value) {
        try {
            Settings.Global.putString(context.getContentResolver(), key, value);
        } catch (Exception e) {
            String baseCommand = "settings put global " + key + " " + value;
            if (testSudo()) {
                sudo(baseCommand);
            } else {
                String adbCommand = "adb shell " + baseCommand;
                Intent intent = new Intent(context, SettingWriteFailed.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("command", adbCommand);
                context.startActivity(intent);
            }
        }
    }

    public static void writeSecure(Context context, String key, String value) {
        try {
            Settings.Secure.putString(context.getContentResolver(), key, value);
        } catch (Exception e) {
            String baseCommand = "settings put secure " + key + " " + value;
            if (testSudo()) {
                sudo(baseCommand);
            } else {
                String adbCommand = "adb shell " + baseCommand;
                Intent intent = new Intent(context, SettingWriteFailed.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("command", adbCommand);
                context.startActivity(intent);
            }
        }
    }

    public static void writeSystem(Context context, String key, String value) {
        try {
            Settings.System.putString(context.getContentResolver(), key, value);
        } catch (Exception e) {
            String baseCommand = "settings put system " + key + " " + value;
            if (testSudo()) {
                sudo(baseCommand);
            } else {
                String adbCommand = "adb shell " + baseCommand;
                Intent intent = new Intent(context, SettingWriteFailed.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("command", adbCommand);
                context.startActivity(intent);
            }
        }
    }

    public static boolean hasPerms(Context context) {
        try {
            Settings.Secure.putString(context.getContentResolver(), "systemui_tuner_setup", "1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
