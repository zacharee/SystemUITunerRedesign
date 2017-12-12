package com.zacharee1.systemuituner.handlers;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zacharee1.systemuituner.util.SettingsUtils;

import java.util.TreeSet;

public class ImmersiveHandler {
    public static final String KEY = "policy_control";
    public static final String FULL = "immersive.full";
    public static final String STATUS = "immersive.status";
    public static final String NAV = "immersive.navigation";
    public static final String PRECONF = "immersive.preconfirms";
    public static final String DISABLED = "immersive.none";

    public static final Uri POLICY_CONTROL = Settings.Global.getUriFor(KEY);

    public static boolean isInImmersive(Context context) {
        String imm = Settings.Global.getString(context.getContentResolver(), KEY);

        return imm != null && !imm.isEmpty() && (
                imm.contains(FULL)
                || imm.contains(STATUS)
                || imm.contains(NAV)
                || imm.contains(PRECONF)
                );
    }

    @Nullable
    public static String getMode(Context context) {
        String imm = Settings.Global.getString(context.getContentResolver(), KEY);
        if (imm == null) imm = "immersive.none";
        imm = imm.replaceAll("=(.+?)$", "");

        return imm;
    }

    public static void setMode(Context context, String type) {
        Log.e("Setting Mode", type);

        if (type.contains(FULL)
                || type.contains(STATUS)
                || type.contains(NAV)
                || type.contains(PRECONF)
                || type.contains(DISABLED)) {

            type = concat(context, type);

            SettingsUtils.writeGlobal(context, KEY, type);
        } else {
            throw new IllegalArgumentException("Invalid Immersive Mode type: " + type);
        }
    }

    private static String concat(Context context, String type) {
        StringBuilder builder = new StringBuilder(type.replace("=*", ""));
        builder.append("=");
        if (isSelecting(context)) {
            builder.append(parseSelectedApps(context, "*"));
        } else {
            builder.append("*");
        }

        Log.e("Options", builder.toString());

        return builder.toString();
    }

    public static String parseSelectedApps(Context context, String def) {
        TreeSet<String> apps = parseSelectedApps(context, new TreeSet<String>());

        if (apps.isEmpty()) return def;
        else {
            StringBuilder ret = new StringBuilder();
            if (isBlacklist(context)) ret.append("apps,");

            for (String app : apps) {
                ret.append(isBlacklist(context) ? "-" : "").append(app).append(",");
            }

            return ret.toString();
        }
    }

    public static TreeSet<String> parseSelectedApps(Context context, TreeSet<String> def) {
        return new TreeSet<>(PreferenceManager.getDefaultSharedPreferences(context).getStringSet("immersive_apps", def));
    }

    public static void addApp(Context context, String add) {
        TreeSet<String> set = new TreeSet<>(PreferenceManager.getDefaultSharedPreferences(context).getStringSet("immersive_apps", new TreeSet<String>()));
        set.add(add);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet("immersive_apps", set).apply();
    }

    public static void removeApp(Context context, String remove) {
        TreeSet<String> set = new TreeSet<>(PreferenceManager.getDefaultSharedPreferences(context).getStringSet("immersive_apps", new TreeSet<String>()));
        set.remove(remove);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet("immersive_apps", set).apply();
    }

    public static boolean isSelecting(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("app_immersive", false);
    }

    public static boolean isBlacklist(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("immersive_blacklist", false);
    }
}
