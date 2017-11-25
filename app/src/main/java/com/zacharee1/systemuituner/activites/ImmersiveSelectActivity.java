package com.zacharee1.systemuituner.activites;

import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.zacharee1.systemuituner.App;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.AppInfo;
import com.zacharee1.systemuituner.misc.ImmersiveHandler;
import com.zacharee1.systemuituner.misc.Utils;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class ImmersiveSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blank);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ProgressBar bar = new ProgressBar(this);
        bar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ((LinearLayout)findViewById(R.id.content_main)).addView(bar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                final TreeMap<String, AppInfo> appMap = new TreeMap<>();

                for (ApplicationInfo info : Utils.getInstalledApps(ImmersiveSelectActivity.this)) {
                    try {
                        if (getPackageManager().getPackageInfo(info.packageName, PackageManager.GET_ACTIVITIES).activities.length > 1) {
                            appMap.put(info.loadLabel(getPackageManager()).toString(),
                                    new AppInfo(info.loadLabel(getPackageManager()).toString(),
                                            info.packageName,
                                            null,
                                            info.loadIcon(getPackageManager()))
                            );
                        }
                    } catch (Exception e) {}
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final SelectorFragment fragment = SelectorFragment.newInstance();
                        fragment.setInfo(appMap);

                        ((LinearLayout)findViewById(R.id.content_main)).removeAllViews();
                        try {
                            getFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
                        } catch (Exception e) {}
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SelectorFragment extends PreferenceFragment {
        TreeMap<String, AppInfo> mInfo;

        public static SelectorFragment newInstance() {
            return new SelectorFragment();
        }

        public void setInfo(TreeMap<String, AppInfo> info) {
            mInfo = info;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_blank);
            populateList();
        }

        public void populateList() {
            TreeSet<String> selectedApps = ImmersiveHandler.parseSelectedApps(getActivity(), new TreeSet<String>());

            for (AppInfo info : mInfo.values()) {
                CheckBoxPreference preference = new CheckBoxPreference(getActivity());
                preference.setTitle(info.appName);
                preference.setSummary(info.packageName);
                preference.setIcon(info.appIcon);
                preference.setKey(info.packageName);
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        boolean isChecked = Boolean.valueOf(o.toString());
                        if (isChecked) {
                            ImmersiveHandler.addApp(getActivity(), preference.getKey());
                        } else {
                            ImmersiveHandler.removeApp(getActivity(), preference.getKey());
                        }
                        return true;
                    }
                });
                preference.setChecked(selectedApps.contains(preference.getKey()));

                getPreferenceScreen().addPreference(preference);
            }
        }
    }
}
