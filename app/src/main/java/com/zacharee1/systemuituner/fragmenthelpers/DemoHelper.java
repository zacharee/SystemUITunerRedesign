package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.prefs.SliderPreference;
import com.zacharee1.systemuituner.prefs.SliderPreferenceEmbedded;
import com.zacharee1.systemuituner.prefs.TimePreference;
import com.zacharee1.systemuituner.misc.SettingsUtils;

import java.util.List;

public class DemoHelper
{
    private final ItemDetailFragment mFragment;

    public DemoHelper(ItemDetailFragment fragment) {
        mFragment = fragment;

//        SliderPreferenceEmbedded batteryLevel = (SliderPreferenceEmbedded) mFragment.findPreference("selected_battery_level");
//        SliderPreferenceEmbedded wifiStrength = (SliderPreferenceEmbedded) mFragment.findPreference("wifi_strength");
//        SliderPreferenceEmbedded mobileStrength = (SliderPreferenceEmbedded) mFragment.findPreference("selected_mobile_strength");
//        SliderPreferenceEmbedded simCount = (SliderPreferenceEmbedded) mFragment.findPreference("sim_count");
//
//        batteryLevel.setMaxProgess(100);
//        wifiStrength.setMaxProgess(4);
//        mobileStrength.setMaxProgess(4);
//        simCount.setMaxProgess(7);

        setPrefListeners();
        setDemoSwitchListener();
    }

    private boolean showNotifs() {
        SwitchPreference showNotifs = (SwitchPreference) mFragment.findPreference("show_notifications");
        return showNotifs.isChecked();
    }

    private boolean showMobile() {
        SwitchPreference showMobile = (SwitchPreference) mFragment.findPreference("show_mobile");
        return showMobile.isChecked();
    }

    private boolean showAirplane() {
        SwitchPreference showAirplane = (SwitchPreference) mFragment.findPreference("show_airplane");
        return showAirplane.isChecked();
    }

    private boolean showWiFi() {
        SwitchPreference showWiFi = (SwitchPreference) mFragment.findPreference("show_wifi");
        return showWiFi.isChecked();
    }

    private boolean batteryCharging() {
        SwitchPreference batteryCharging = (SwitchPreference) mFragment.findPreference("battery_charging");
        return batteryCharging.isChecked();
    }

    private boolean mobileFully() {
        SwitchPreference mobileFully = (SwitchPreference) mFragment.findPreference("mobile_fully_connected");
        return mobileFully.isChecked();
    }

    private boolean wifiFully() {
        SwitchPreference wifiFully = (SwitchPreference) mFragment.findPreference("wifi_fully_connected");
        return wifiFully.isChecked();
    }

    private boolean noSim() {
        SwitchPreference noSim = (SwitchPreference) mFragment.findPreference("no_sim");
        return noSim.isChecked();
    }

    private boolean location() {
        SwitchPreference location = (SwitchPreference) mFragment.findPreference("location_demo");
        return location.isChecked();
    }

    private boolean alarm() {
        SwitchPreference alarm = (SwitchPreference) mFragment.findPreference("alarm_demo");
        return alarm.isChecked();
    }

    private boolean sync() {
        SwitchPreference sync = (SwitchPreference) mFragment.findPreference("sync_demo");
        return sync.isChecked();
    }

    private boolean tty() {
        SwitchPreference tty = (SwitchPreference) mFragment.findPreference("tty_demo");
        return tty.isChecked();
    }

    private boolean eri() {
        SwitchPreference eri = (SwitchPreference) mFragment.findPreference("eri_demo");
        return eri.isChecked();
    }

    private boolean mute() {
        SwitchPreference mute = (SwitchPreference) mFragment.findPreference("mute_demo");
        return mute.isChecked();
    }

    private boolean spkphone() {
        SwitchPreference spkphone = (SwitchPreference) mFragment.findPreference("speakerphone_demo");
        return spkphone.isChecked();
    }

    private int batteryLevel() {
        SliderPreferenceEmbedded batteryLevel = (SliderPreferenceEmbedded) mFragment.findPreference("selected_battery_level");
        return batteryLevel.getCurrentProgress();
    }

    private int wifiLevel() {
        SliderPreferenceEmbedded wifiStrength = (SliderPreferenceEmbedded) mFragment.findPreference("wifi_strength");
        return wifiStrength.getCurrentProgress();
    }

    private int mobileLevel() {
        SliderPreferenceEmbedded mobileStrength = (SliderPreferenceEmbedded) mFragment.findPreference("selected_mobile_strength");
        return mobileStrength.getCurrentProgress();
    }

    private int simCount() {
        SliderPreferenceEmbedded simCount = (SliderPreferenceEmbedded) mFragment.findPreference("sim_count");
        return simCount.getCurrentProgress() + 1;
    }

    private int timeHour() {
        TimePreference time = (TimePreference) mFragment.findPreference("selected_time");
        return time.getSavedHour();
    }

    private int timeMinute() {
        TimePreference time = (TimePreference) mFragment.findPreference("selected_time");
        return time.getSavedMinute();
    }

    private long time() {
        TimePreference time = (TimePreference) mFragment.findPreference("selected_time");
        return time.getSavedTimeMillis();
    }

    private String mobileType() {
        ListPreference mobileType = (ListPreference) mFragment.findPreference("mobile_type");
        return mobileType.getValue();
    }

    private String statStyle() {
        ListPreference statStyle = (ListPreference) mFragment.findPreference("status_bar_style");
        return statStyle.getValue();
    }

    private String volumeIcon() {
        ListPreference volumeIcon = (ListPreference) mFragment.findPreference("volume_icon");
        return volumeIcon.getValue();
    }

    private String btIcon() {
        ListPreference btIcon = (ListPreference) mFragment.findPreference("bluetooth_icon");
        return btIcon.getValue();
    }

    private void setPrefListeners() {
        final Preference enableDemo = mFragment.findPreference("sysui_demo_allowed");
        enableDemo.setEnabled(Settings.Global.getInt(mFragment.getContext().getContentResolver(), "sysui_demo_allowed", 0) == 0);
        enableDemo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                if (mFragment.getActivity().checkCallingOrSelfPermission("android.permission.DUMP") == PackageManager.PERMISSION_GRANTED) {
                    SettingsUtils.writeGlobal(mFragment.getContext(), preference.getKey(), "1");
                    mFragment.findPreference("show_demo").setEnabled(true);
                } else {
                    Toast.makeText(mFragment.getContext(), mFragment.getResources().getString(R.string.grant_dump_perm), Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    private void setDemoSwitchListener() {
        SwitchPreference demo = (SwitchPreference) mFragment.findPreference("show_demo");

        demo.setEnabled(Settings.Global.getInt(mFragment.getContext().getContentResolver(), "sysui_demo_allowed", 0) == 1);

        demo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                if (Boolean.valueOf(o.toString())) {
                    showDemo();
                } else {
                    hideDemo();
                }

                return true;
            }
        });
    }

    private void showDemo() {
        try {
            //create new Intent and add relevant data to show Demo Mode with specified options
            Intent intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "enter");
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "clock");
//            intent.putExtra("hhmm", (timeHour() < 10 ? "0" + timeHour() : timeHour()) + "" + (timeMinute() < 10 ? "0" + timeMinute() : timeMinute()));
            intent.putExtra("millis", time() + "");
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "network");
            intent.putExtra("mobile", showMobile() ? "show" : "hide");
            intent.putExtra("fully", mobileFully());
            intent.putExtra("level", mobileLevel() + "");
            intent.putExtra("datatype", mobileType());
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

            intent = new Intent("com.android.systemuui.demo");
            intent.putExtra("command", "network");
            intent.putExtra("sims", simCount() + "");
            intent.putExtra("nosim", noSim() ? "show" : "hide");
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "network");
            intent.putExtra("wifi", showWiFi() ? "show" : "hide");
            intent.putExtra("fully", wifiFully());
            intent.putExtra("level", wifiLevel() + "");
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "network");
            intent.putExtra("airplane", showAirplane() ? "show" : "hide");
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "battery");
            intent.putExtra("level", batteryLevel() + "");
            intent.putExtra("plugged", batteryCharging() + "");
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "notifications");
            intent.putExtra("visible", showNotifs() + "");
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

            intent = new Intent("com.android.systemui.demo");
            intent.putExtra("command", "bars");
            intent.putExtra("mode", statStyle());
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

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
            mFragment.getActivity().sendBroadcast(intent);

            logIntent(intent);

            disableOtherPreferences(true);
        } catch (Exception e) {
            Log.e("Demo", e.getMessage());
            e.printStackTrace();
        }
    }

    private void logIntent(Intent intent) {
        Log.e("Intent", intent.getExtras().toString());
    }

    private void hideDemo() {
        Intent intent = new Intent("com.android.systemui.demo");
        intent.putExtra("command", "exit");
        mFragment.getActivity().sendBroadcast(intent);

        disableOtherPreferences(false);
    }

    private void disableOtherPreferences(boolean disable) {
        for (int i = 0; i < mFragment.getPreferenceScreen().getRootAdapter().getCount(); i++) {
            Object item = mFragment.getPreferenceScreen().getRootAdapter().getItem(i);

            if (item instanceof Preference) {
                Preference preference = (Preference) item;

                if (preference.hasKey() && !(preference.getKey().equals("sysui_demo_allowed") || preference.getKey().equals("show_demo"))) {
                    preference.setEnabled(!disable);
                }
            }
        }
    }
}
