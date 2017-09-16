package com.zacharee1.systemuituner.misc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;

import com.zacharee1.systemuituner.prefs.SliderPreferenceEmbedded;
import com.zacharee1.systemuituner.prefs.TimePreference;

public class DemoHandler {
    private Context mContext;
    private SharedPreferences mPrefs;

    public DemoHandler(Context context) {
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public boolean isAllowed() {
        return Settings.Global.getInt(mContext.getContentResolver(), "sysui_demo_allowed", 0) == 1;
    }

    public boolean isEnabled() {
        return Settings.Global.getInt(mContext.getContentResolver(), "sysui_tuner_demo_on", 0) == 1;
    }

    public void showDemo() {
        try {
            //create new Intent and add relevant data to show Demo Mode with specified options
            Intent intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "enter");
            mContext.sendBroadcast(intent);

//            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "clock");
//            intent.putExtra("hhmm", (timeHour() < 10 ? "0" + timeHour() : timeHour()) + "" + (timeMinute() < 10 ? "0" + timeMinute() : timeMinute()));
            intent.putExtra("millis", time() + "");
            mContext.sendBroadcast(intent);

//            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "network");
            intent.putExtra("mobile", showMobile() ? "show" : "hide");
            intent.putExtra("fully", mobileFully());
            intent.putExtra("level", mobileLevel() + "");
            intent.putExtra("datatype", mobileType());
            mContext.sendBroadcast(intent);

//            logIntent(intent);

            intent = new Intent("com.android.systemuui.demo");
            intent.putExtra("command", "network");
            intent.putExtra("sims", simCount() + "");
            intent.putExtra("nosim", noSim() ? "show" : "hide");
            mContext.sendBroadcast(intent);

//            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "network");
            intent.putExtra("wifi", showWiFi() ? "show" : "hide");
            intent.putExtra("fully", wifiFully());
            intent.putExtra("level", wifiLevel() + "");
            mContext.sendBroadcast(intent);

//            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "network");
            intent.putExtra("airplane", showAirplane() ? "show" : "hide");
            mContext.sendBroadcast(intent);

//            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "battery");
            intent.putExtra("level", batteryLevel() + "");
            intent.putExtra("plugged", batteryCharging() + "");
            mContext.sendBroadcast(intent);

//            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "notifications");
            intent.putExtra("visible", showNotifs() + "");
            mContext.sendBroadcast(intent);

//            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "bars");
            intent.putExtra("mode", statStyle());
            mContext.sendBroadcast(intent);

//            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "status");
            intent.putExtra("volume", volumeIcon());
            intent.putExtra("bluetooth", btIcon());
            intent.putExtra("location", location() ? "show" : "hide");
            intent.putExtra("alarm", alarm() ? "show" : "hide");
            intent.putExtra("sync", sync() ? "show" : "hide");
            intent.putExtra("tty", tty() ? "show" : "hide");
            intent.putExtra("eri", eri() ? "show" : "hide");
            intent.putExtra("mute", mute() ? "show" : "hide");
            intent.putExtra("speakerphone", spkphone() ? "show" : "hide");
            mContext.sendBroadcast(intent);

            Settings.Global.putInt(mContext.getContentResolver(), "sysui_tuner_demo_on", 1);
        } catch (Exception e) {
            Log.e("Demo", e.getMessage());
            e.printStackTrace();
        }
    }

    public void hideDemo() {
        Intent intent = new Intent("com.android.systemui.demo");
        intent.putExtra("command", "exit");
        mContext.sendBroadcast(intent);

        Settings.Global.putInt(mContext.getContentResolver(), "sysui_tuner_demo_on", 0);
    }

    private boolean showNotifs() {
        return mPrefs.getBoolean("show_notifications", false);
    }

    private boolean showMobile() {
        return mPrefs.getBoolean("show_mobile", false);
    }

    private boolean showAirplane() {
        return mPrefs.getBoolean("show_airplane", false);
    }

    private boolean showWiFi() {
        return mPrefs.getBoolean("show_wifi", false);
    }

    private boolean batteryCharging() {
        return mPrefs.getBoolean("battery_charging", false);
    }

    private boolean mobileFully() {
        return mPrefs.getBoolean("mobile_fully_connected", false);
    }

    private boolean wifiFully() {
        return mPrefs.getBoolean("wifi_fully_connected", false);
    }

    private boolean noSim() {
        return mPrefs.getBoolean("no_sim", false);
    }

    private boolean location() {
        return mPrefs.getBoolean("location_demo", false);
    }

    private boolean alarm() {
        return mPrefs.getBoolean("alarm_demo", false);
    }

    private boolean sync() {
        return mPrefs.getBoolean("sync_demo", false);
    }

    private boolean tty() {
        return mPrefs.getBoolean("tty_demo", false);
    }

    private boolean eri() {
        return mPrefs.getBoolean("eri_demo", false);
    }

    private boolean mute() {
        return mPrefs.getBoolean("mute_demo", false);
    }

    private boolean spkphone() {
        return mPrefs.getBoolean("speakerphone_demo", false);
    }

    private int batteryLevel() {
        return mPrefs.getInt("selected_battery_level", 100);
    }

    private int wifiLevel() {
        return mPrefs.getInt("wifi_strength", 4);
    }

    private int mobileLevel() {
        return mPrefs.getInt("selected_mobile_strength", 4);
    }

    private int simCount() {
        return mPrefs.getInt("sim_count", 0) + 1;
    }

    private long time() {
        return mPrefs.getLong("selected_time", System.currentTimeMillis());
    }

    private String mobileType() {
        return mPrefs.getString("mobile_type", "lte");
    }

    private String statStyle() {
        return mPrefs.getString("status_bar_style", "default");
    }

    private String volumeIcon() {
        return mPrefs.getString("volume_icon", "hidden");
    }

    private String btIcon() {
        return mPrefs.getString("bluetooth_icon", "hidden");
    }
}
