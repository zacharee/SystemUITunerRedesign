package com.zacharee1.systemuituner.misc;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;

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

        return imm != null && (
                imm.contains(FULL)
                || imm.contains(STATUS)
                || imm.contains(NAV)
                || imm.contains(PRECONF)
                );
    }

    @Nullable
    public static String getMode(Context context, boolean keepEqStar) {
        String imm = Settings.Global.getString(context.getContentResolver(), KEY);

        if (imm != null && !keepEqStar) {
            imm = imm.replace("=*", "");
        }

        return imm;
    }

    public static void setMode(Context context, String type) {
        if (type.contains(FULL)
                || type.contains(STATUS)
                || type.contains(NAV)
                || type.contains(PRECONF)
                || type.contains(DISABLED)) {

            if (!type.contains("=*")) type = type.concat("=*");

            SettingsUtils.writeGlobal(context, KEY, type);
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
    }
}
