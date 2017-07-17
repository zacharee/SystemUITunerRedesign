package com.zacharee1.systemuituner.Utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.zacharee1.systemuituner.Utils.SuUtils.testSudo;
import static com.zacharee1.systemuituner.Utils.SuUtils.sudo;

/**
 * Created by Zacha on 7/17/2017.
 */

public class SettingsUtils
{
    public static void writeGlobal(Context context, String key, String value) {
        try {
            Settings.Global.putString(context.getContentResolver(), key, value);
        } catch (Exception e) {
            if (testSudo()) {
                sudo("settings put global " + key + " " + value);
            } else {
                //start help/info activity
            }
        }
    }

    public static void writeSecure(Context context, String key, String value) {
        try {
            Settings.Secure.putString(context.getContentResolver(), key, value);
        } catch (Exception e) {
            if (testSudo()) {
                sudo("settings put secure " + key + " " + value);
            } else {
                //start help/info activity
            }
        }
    }

    public static void writeSystem(Context context, String key, String value) {
        try {
            Settings.System.putString(context.getContentResolver(), key, value);
        } catch (Exception e) {
            if (testSudo()) {
                sudo("settings put system " + key + " " + value);
            } else {
                //start info/help activity
            }
        }
    }
}
