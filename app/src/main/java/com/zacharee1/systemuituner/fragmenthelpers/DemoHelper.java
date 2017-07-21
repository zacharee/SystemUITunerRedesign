package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.prefs.SliderPreference;
import com.zacharee1.systemuituner.prefs.TimePreference;
import com.zacharee1.systemuituner.misc.SettingsUtils;

/**
 * Created by Zacha on 7/16/2017.
 */

public class DemoHelper
{
    private ItemDetailFragment mFragment;

    private boolean mShowNotifs;
    private boolean mBatteryCharging;
    private boolean mShowAirplane;

    private int mBatteryLevel;
    private int mWifiStrength;
    private int mMobileStrength;

    private String mMobileType;
    private String mStatStyle;

    private int mHour;
    private int mMinute;

    public DemoHelper(ItemDetailFragment fragment) {
        mFragment = fragment;

        setSwitchListeners();
        setSliderListeners();
        setPrefListeners();
        setListListeners();
        setDemoSwitchListener();
        setTimeListeners();
    }

    private void setSwitchListeners() {
        SwitchPreference showNotifs = (SwitchPreference) mFragment.findPreference("show_notifications");
        SwitchPreference batteryCharging = (SwitchPreference) mFragment.findPreference("battery_charging");
        SwitchPreference showAirplane = (SwitchPreference) mFragment.findPreference("show_airplane");

        mShowNotifs = showNotifs.isChecked();
        mBatteryCharging = batteryCharging.isChecked();
        mShowAirplane = showAirplane.isChecked();

        showNotifs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                mShowNotifs = Boolean.valueOf(o.toString());
                return true;
            }
        });

        batteryCharging.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                mBatteryCharging = Boolean.valueOf(o.toString());
                return true;
            }
        });

        showAirplane.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                mShowAirplane = Boolean.valueOf(o.toString());
                return true;
            }
        });
    }

    private void setSliderListeners() {
        SliderPreference batteryLevel = (SliderPreference) mFragment.findPreference("selected_battery_level");
        SliderPreference wifiStrength = (SliderPreference) mFragment.findPreference("wifi_strength");
        SliderPreference mobileStrength = (SliderPreference) mFragment.findPreference("selected_mobile_strength");

        mBatteryLevel = batteryLevel.getSavedProgress();
        mWifiStrength = wifiStrength.getSavedProgress();
        mMobileStrength = mobileStrength.getSavedProgress();

        batteryLevel.setMaxProgess(100);
        wifiStrength.setMaxProgess(4);
        mobileStrength.setMaxProgess(4);

        batteryLevel.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                mBatteryLevel = Integer.decode(o.toString());
                return true;
            }
        });

        wifiStrength.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                mWifiStrength = Integer.decode(o.toString());
                return true;
            }
        });

        mobileStrength.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                mMobileStrength = Integer.decode(o.toString());
                return true;
            }
        });
    }

    private void setPrefListeners() {
        final Preference enableDemo = mFragment.findPreference("sysui_demo_allowed");
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

    private void setListListeners() {
        ListPreference mobileType = (ListPreference) mFragment.findPreference("mobile_type");
        ListPreference statStyle = (ListPreference) mFragment.findPreference("status_bar_style");

        mMobileType = mobileType.getValue();
        mStatStyle = statStyle.getValue();

        mobileType.setPersistent(true);
        statStyle.setPersistent(true);

        mobileType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                mMobileType = o.toString();
                return true;
            }
        });

        statStyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                mStatStyle = o.toString();
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

    private void setTimeListeners() {
        TimePreference time = (TimePreference) mFragment.findPreference("selected_time");

        mHour = time.getSavedHour();
        mMinute = time.getSavedMinute();

        time.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o)
            {
                Bundle bundle = (Bundle) o;
                mHour = bundle.getInt("hour");
                mMinute = bundle.getInt("minute");
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

            intent.putExtra("command", "clock");
            intent.putExtra("hhmm", (mHour < 10 ? "0" + mHour : mHour) + "" + (mMinute < 10 ? "0" + mMinute : mMinute));

            Log.e("HHMM", intent.getStringExtra("hhmm"));

            mFragment.getActivity().sendBroadcast(intent);

            intent.putExtra("command", "network");
            intent.putExtra("mobile", "show");
            intent.putExtra("fully", "true");
            intent.putExtra("level", mMobileStrength + "");
            intent.putExtra("datatype", mMobileType);
            mFragment.getActivity().sendBroadcast(intent);

            intent.removeExtra("mobile");
            intent.removeExtra("datatype");
            intent.putExtra("wifi", "show");
            intent.putExtra("fully", "true");
            intent.putExtra("level", mWifiStrength + "");
            mFragment.getActivity().sendBroadcast(intent);

            intent.putExtra("airplane", mShowAirplane ? "show" : "hide");
            mFragment.getActivity().sendBroadcast(intent);

            intent.putExtra("command", "battery");
            intent.putExtra("level", mBatteryLevel + "");
            intent.putExtra("plugged", mBatteryCharging + "");
            mFragment.getActivity().sendBroadcast(intent);

            intent.putExtra("command", "notifications");
            intent.putExtra("visible", mShowNotifs + "");
            mFragment.getActivity().sendBroadcast(intent);

            intent.putExtra("command", "bars");
            intent.putExtra("mode", mStatStyle);
            mFragment.getActivity().sendBroadcast(intent);

            disableOtherPreferences(true);
        } catch (Exception e) {
            Log.e("Demo", e.getMessage());
        }
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
