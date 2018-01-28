package com.zacharee1.systemuituner.activites.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.activites.instructions.TaskerInstructionActivity;
import com.zacharee1.systemuituner.handlers.RecreateHandler;
import com.zacharee1.systemuituner.services.SafeModeService;
import com.zacharee1.systemuituner.util.Utils;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity
{

    public final static String RECREATE_ACTIVITY = "recreate_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(Utils.isInDarkMode(this) ? R.style.AppTheme_Dark : R.style.AppTheme);

        RecreateHandler.onCreate(this);

        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

    @Override
    protected void onDestroy()
    {
        RecreateHandler.onDestroy(this);
        super.onDestroy();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane()
    {
        return false;
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        Log.e("Gen", GeneralPreferenceFragment.class.getName());
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_general);
            setHasOptionsMenu(true);
            setSwitchListeners();
            setPreferenceListeners();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                getActivity().finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void setSwitchListeners() {
            SwitchPreference darkMode = (SwitchPreference) findPreference("dark_mode");
            SwitchPreference taskerEnabled = (SwitchPreference) findPreference("tasker_support_enabled");
            SwitchPreference safeMode = (SwitchPreference) findPreference("safe_mode");

            darkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    Intent intent = new Intent();
                    intent.setAction(RECREATE_ACTIVITY);
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                    return true;
                }
            });

            taskerEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    boolean enabled = Boolean.valueOf(o.toString());
                    findPreference("tasker_instructions").setEnabled(enabled);
                    return true;
                }
            });

            safeMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (Boolean.valueOf(newValue.toString())) {
                        getActivity().stopService(new Intent(getActivity(), SafeModeService.class));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            getActivity().startService(new Intent(getActivity(), SafeModeService.class));
                        } else {
                            getActivity().startForegroundService(new Intent(getActivity(), SafeModeService.class));
                        }
                    } else {
                        getActivity().stopService(new Intent(getActivity(), SafeModeService.class));
                    }

                    return true;
                }
            });
        }

        private void setPreferenceListeners() {
            Preference tasker = findPreference("tasker_instructions");

            tasker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    startActivity(new Intent(getContext(), TaskerInstructionActivity.class));
                    return true;
                }
            });

            tasker.setEnabled(getPreferenceManager().getSharedPreferences().getBoolean("tasker_support_enabled", false));
        }
    }
}
