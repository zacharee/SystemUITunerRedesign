package com.zacharee1.systemuituner;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.zacharee1.systemuituner.activites.ItemListActivity;
import com.zacharee1.systemuituner.activites.MainActivity;
import com.zacharee1.systemuituner.activites.SetupActivity;
import com.zacharee1.systemuituner.misc.SettingsUtils;
import com.zacharee1.systemuituner.misc.SuUtils;

public class LauncherActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!SettingsUtils.hasSpecificPerm(this, Manifest.permission.WRITE_SECURE_SETTINGS)) {
            if (SuUtils.testSudo()) {
                SuUtils.sudo("pm grant com.zacharee1.systemuituner android.permission.WRITE_SECURE_SETTINGS ; " +
                        "pm grant com.zacharee1.systemuituner android.permission.DUMP ; " +
                        "pm grant com.zacharee1.systemuituner android.permission.PACKAGE_USAGE_STATS");
                startUp();
            } else {
                Intent intent = new Intent(this, SetupActivity.class);
                intent.putExtra("permission_needed", new String[] { Manifest.permission.WRITE_SECURE_SETTINGS });
                startActivity(intent);
                finish();
            }
        } else {
            startUp();
        }
    }

    private void startUp() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        boolean firstStart = sharedPreferences.getBoolean("first_start", true);
        if (firstStart && Build.MANUFACTURER.toLowerCase().contains("samsung") && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            sharedPreferences.edit().putBoolean("safe_mode", true).apply();
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.notice))
                    .setMessage(getResources().getString(R.string.safe_mode_auto_enabled))
                    .setPositiveButton(getResources().getString(R.string.ok), null)
                    .show();
        }
        sharedPreferences.edit().putBoolean("first_start", false).apply();

        if (sharedPreferences.getBoolean("hide_welcome_screen", false)) {
            startActivity(new Intent(this, ItemListActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
