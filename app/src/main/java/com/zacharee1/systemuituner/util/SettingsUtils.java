package com.zacharee1.systemuituner.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.activites.info.SettingWriteFailed;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("UnusedReturnValue")
public class SettingsUtils
{
    public static boolean writeGlobal(Context context, String key, String value) {
        try {
            Settings.Global.putString(context.getContentResolver(), key, value);
            return true;
        } catch (Exception e) {
            String baseCommand = "settings put global " + key + " " + value;
            if (SuUtils.testSudo()) {
                SuUtils.sudo(baseCommand);
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
            if (SuUtils.testSudo()) {
                SuUtils.sudo(baseCommand);
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
            if (SuUtils.testSudo()) {
                SuUtils.sudo(baseCommand);
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
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            ArrayList<String> perms = new ArrayList<>(Arrays.asList(packageInfo.requestedPermissions));

            for (String permission : perms) {
                if (!hasSpecificPerm(context, permission)) return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean hasSpecificPerm(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasSpecificPerms(Context context, String[] permissions) {
        for (String perm : permissions) {
            if (context.checkCallingOrSelfPermission(perm) == PackageManager.PERMISSION_DENIED) return false;
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
