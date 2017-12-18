package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.activites.apppickers.AppsListActivity;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.util.SettingsUtils;

public class LockHelper extends BaseHelper {
    public static final String LOCKSCREEN_SHORTCUTS = "lockscreen_shortcuts";
    public static final String OREO_NEEDED = "oreo_needed";
    public static final String KEYGUARD_LEFT_UNLOCK = "sysui_keyguard_left_unlock";
    public static final String KEYGUARD_RIGHT_UNLOCK = "sysui_keyguard_right_unlock";
    public static final String KEYGUARD_LEFT = "sysui_keyguard_left";
    public static final String KEYGUARD_RIGHT = "sysui_keyguard_right";
    public static final String CHOOSE_LEFT = "choose_left";
    public static final String CHOOSE_RIGHT = "choose_right";
    public static final String EXTRA_ISLEFT = "isLeft";
    public static final String RESET_LEFT = "reset_left";
    public static final String RESET_RIGHT = "reset_right";

    public LockHelper(ItemDetailFragment fragment) {
        super(fragment);
        setEnabled();
        setLockIconStuff();
        setShortcutSwitchListeners();
        setResetListeners();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onResume() {
        setLockSummaryAndIcon();
    }

    private void setEnabled() {
        PreferenceCategory shortcuts = (PreferenceCategory) findPreference(LOCKSCREEN_SHORTCUTS);
        Preference oreoMsg = findPreference(OREO_NEEDED);
        boolean isOreo = Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1;

        shortcuts.setEnabled(isOreo);
        if (isOreo) shortcuts.removePreference(oreoMsg);
    }

    private void setShortcutSwitchListeners() {
        SwitchPreference left = (SwitchPreference) findPreference(KEYGUARD_LEFT_UNLOCK);
        SwitchPreference right = (SwitchPreference) findPreference(KEYGUARD_RIGHT_UNLOCK);
        Preference.OnPreferenceChangeListener    listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsUtils.writeSecure(getContext(), preference.getKey(), newValue.toString());
                return true;
            }
        };

        left.setOnPreferenceChangeListener(listener);
        right.setOnPreferenceChangeListener(listener);
    }

    private void setLockIconStuff() {
        setLockSummaryAndIcon();

        final Preference left = findPreference(CHOOSE_LEFT);
        left.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent activity = new Intent(getContext(), AppsListActivity.class);
                activity.putExtra(EXTRA_ISLEFT, true);
                startActivityForResult(activity, 1337);
                return true;
            }
        });

        Preference right = findPreference(CHOOSE_RIGHT);
        right.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent activity = new Intent(getContext(), AppsListActivity.class);
                activity.putExtra(EXTRA_ISLEFT, false);
                startActivityForResult(activity, 1337);
                return true;
            }
        });
    }

    private void setLockSummaryAndIcon() {
        Preference leftLock = findPreference(CHOOSE_LEFT);
        Preference rightLock = findPreference(CHOOSE_RIGHT);

        String leftSum = Settings.Secure.getString(getContext().getContentResolver(), KEYGUARD_LEFT);
        String rightSum = Settings.Secure.getString(getContext().getContentResolver(), KEYGUARD_RIGHT);

        String[] leftStuff = null;
        String[] rightStuff = null;

        if (leftSum != null) leftStuff = leftSum.split("[/]");
        if (rightSum != null) rightStuff = rightSum.split("[/]");

//        if (leftSum != null) leftSum = leftSum.replace("/.", "");
//        if (rightSum != null) rightSum = rightSum.replace("/.", "");

        leftLock.setSummary(leftSum);
        rightLock.setSummary(rightSum);

        PackageManager pm = getActivity().getPackageManager();

        Drawable unknown = getContext().getResources().getDrawable(R.drawable.ic_help_outline_black_24dp, null);
        unknown.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        try {
            leftLock.setIcon(pm.getActivityIcon(new ComponentName(leftStuff[0], leftStuff[1])));
        } catch (Exception e) {
            leftLock.setIcon(unknown);
        }

        try {
            rightLock.setIcon(pm.getActivityIcon(new ComponentName(rightStuff[0], rightStuff[1])));
        } catch (Exception e) {
            rightLock.setIcon(unknown);
        }
    }

    private void setResetListeners() {
        Preference resetLeft = findPreference(RESET_LEFT);
        Preference resetRight = findPreference(RESET_RIGHT);

        resetLeft.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SettingsUtils.writeSecure(getContext(), KEYGUARD_LEFT, "");
                setLockSummaryAndIcon();
                return true;
            }
        });
        resetRight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SettingsUtils.writeSecure(getContext(), KEYGUARD_RIGHT, "");
                setLockSummaryAndIcon();
                return true;
            }
        });
    }
}
