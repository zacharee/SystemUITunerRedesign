package com.zacharee1.systemuituner.misc;

import android.app.AppOpsManager;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by Zacha on 7/21/2017.
 */

public class Reflectors
{
    public static int checkOpNoThrow(int op, int uid, String packageName, Context context) {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        try
        {
            Method m = appOpsManager.getClass().getMethod("checkOpNoThrow", int.class, int.class, String.class);
            int result = (Integer) m.invoke(appOpsManager, op, uid, packageName);
            Log.e("RESULT", result + "");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return AppOpsManager.MODE_ERRORED;
    }
}
