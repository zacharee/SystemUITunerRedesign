package com.zacharee1.systemuituner.misc;

import android.content.pm.PackageManager;

/**
 * Created by Zacha on 7/19/2017.
 */

public class Utils
{
    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
