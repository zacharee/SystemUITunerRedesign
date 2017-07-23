package com.zacharee1.systemuituner.misc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;

import com.zacharee1.systemuituner.activites.SettingWriteFailed;

import static com.zacharee1.systemuituner.misc.SuUtils.testSudo;
import static com.zacharee1.systemuituner.misc.SuUtils.sudo;

@SuppressWarnings("UnusedReturnValue")
public class SettingsUtils
{
    public static boolean writeGlobal(Context context, String key, String value) {
        try {
            Settings.Global.putString(context.getContentResolver(), key, value);
            return true;
        } catch (Exception e) {
            String baseCommand = "settings put global " + key + " " + value;
            if (testSudo()) {
                sudo(baseCommand);
                return true;
            } else {
                String adbCommand = "adb shell " + baseCommand;
                Intent intent = new Intent(context, SettingWriteFailed.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("command", adbCommand);
                context.startActivity(intent);
                return false;
            }
        }
    }

    public static boolean writeSecure(Context context, String key, String value) {
        try {
            Settings.Secure.putString(context.getContentResolver(), key, value);
            return true;
        } catch (Exception e) {
            String baseCommand = "settings put secure " + key + " " + value;
            if (testSudo()) {
                sudo(baseCommand);
                return true;
            } else {
                String adbCommand = "adb shell " + baseCommand;
                Intent intent = new Intent(context, SettingWriteFailed.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("command", adbCommand);
                context.startActivity(intent);
                return false;
            }
        }
    }

    public static boolean writeSystem(Context context, String key, String value) {
        try {
            Settings.System.putString(context.getContentResolver(), key, value);
            return true;
        } catch (Exception e) {
            String baseCommand = "settings put system " + key + " " + value;
            if (testSudo()) {
                sudo(baseCommand);
                return true;
            } else {
                String adbCommand = "adb shell " + baseCommand;
                Intent intent = new Intent(context, SettingWriteFailed.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("command", adbCommand);
                context.startActivity(intent);
                return false;
            }
        }
    }

    public static boolean hasPerms(Context context) {
        String permission = "android.permission.WRITE_SECURE_SETTINGS";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
