package com.zacharee1.systemuituner.activites;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

        getFragmentManager().beginTransaction().replace(R.id.content_main, SelectorFragment.newInstance()).commit();
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
        public static SelectorFragment newInstance() {
            return new SelectorFragment();
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_blank);
            populateList();
        }

        private void populateList() {
            TreeMap<String, AppInfo> appMap = new TreeMap<>();

            for (ApplicationInfo info : Utils.getInstalledApps(getActivity())) {
                try {
                    if (getActivity().getPackageManager().getPackageInfo(info.packageName, PackageManager.GET_ACTIVITIES).activities.length > 1) {
                        appMap.put(info.loadLabel(getActivity().getPackageManager()).toString(),
                                new AppInfo(info.loadLabel(getActivity().getPackageManager()).toString(),
                                        info.packageName,
                                        null,
                                        info.loadIcon(getActivity().getPackageManager()))
                        );
                    }
                } catch (Exception e) {}
            }

            TreeSet<String> selectedApps = ImmersiveHandler.parseSelectedApps(getActivity(), new TreeSet<String>());

            for (AppInfo info : appMap.values()) {
                CheckBoxPreference preference = new CheckBoxPreference(getActivity());
                preference.setTitle(info.appName);
                preference.setSummary(info.packageName);
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
