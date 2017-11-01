package com.zacharee1.systemuituner.misc;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class AppInfo {
    public String appName;
    public String packageName;
    public String componentName;
    public Drawable appIcon;

    public AppInfo(String aName, String pName, String cName, Drawable aIcon) {
        appName = aName;
        packageName = pName;
        componentName = cName;
        appIcon = aIcon;
    }
}
