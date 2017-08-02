package com.zacharee1.systemuituner.misc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.Toast;

import com.zacharee1.systemuituner.activites.SettingWriteFailed;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;

import java.util.ArrayList;
import java.util.Arrays;

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
        String secureSettings = "android.permission.WRITE_SECURE_SETTINGS";
        int secureVal = context.checkCallingOrSelfPermission(secureSettings);
        if (secureVal != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        String dump = "android.permission.DUMP";
        int dumpVal = context.checkCallingOrSelfPermission(dump);
        if (dumpVal != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        String usageStats = "android.permission.PACKAGE_USAGE_STATS";
        int statsVal = context.checkCallingOrSelfPermission(usageStats);
        if (statsVal != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        return true;
    }

    public static void changeBlacklist(String key, boolean value, Context context) {
        if (key != null) {
            String currentBL = Settings.Secure.getString(context.getContentResolver(), "icon_blacklist");
            if (currentBL == null) currentBL = "";

            if (!value) {
                if (currentBL.isEmpty()) {
                    currentBL = key;
                } else {
                    currentBL = currentBL.concat("," + key);
                }
            } else {
                ArrayList<String> blItems = new ArrayList<>(Arrays.asList(currentBL.split("[,]")));
                ArrayList<String> keyItems = new ArrayList<>(Arrays.asList(key.split("[,]")));

                for (String s : keyItems) {
                    if (blItems.contains(s)) {
                        blItems.remove(s);
                    }
                }

                currentBL = blItems.toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(" ", "");
            }

            SettingsUtils.writeSecure(context, "icon_blacklist", currentBL);
        }
    }

    public static void shouldSetSwitchChecked(ItemDetailFragment fragment) {
        String blString = Settings.Secure.getString(fragment.getActivity().getContentResolver(), "icon_blacklist");
        if (blString == null) blString = "";

        ArrayList<String> blItems = new ArrayList<>(Arrays.asList(blString.split("[,]")));

        for (int i = 0; i < fragment.getPreferenceScreen().getRootAdapter().getCount(); i++) {
            Object o = fragment.getPreferenceScreen().getRootAdapter().getItem(i);

            if (o instanceof SwitchPreference && !((SwitchPreference)o).getTitle().toString().toLowerCase().contains("high brightness warning")) {
                SwitchPreference pref = (SwitchPreference) o;

                pref.setChecked(true);

                if (!blString.isEmpty()) {
                    String key = pref.getKey();

                    if (key != null) {
                        ArrayList<String> keyItems = new ArrayList<>(Arrays.asList(key.split("[,]")));

                        for (String s : keyItems) {
                            if (blItems.contains(s)) {
                                pref.setChecked(false);
                            }
                        }
                    }
                }
            }
        }
    }
}
