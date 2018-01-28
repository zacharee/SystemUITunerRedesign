package com.zacharee1.systemuituner;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.zacharee1.systemuituner.activites.info.IntroActivity;
import com.zacharee1.systemuituner.activites.instructions.SetupActivity;
import com.zacharee1.systemuituner.util.SuUtils;
import com.zacharee1.systemuituner.util.Utils;

import io.fabric.sdk.android.Fabric;

public class LauncherActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean("show_intro", true)) {
            startActivity(new Intent(this, IntroActivity.class));
        } else {
            String[] perms = new String[]{
                    Manifest.permission.WRITE_SECURE_SETTINGS,
                    Manifest.permission.DUMP,
                    Manifest.permission.PACKAGE_USAGE_STATS
            };

            String[] ret;

            if ((ret = Utils.checkPermissions(this, perms)).length > 0) {
                if (SuUtils.testSudo()) {
                    SuUtils.sudo("pm grant com.zacharee1.systemuituner android.permission.WRITE_SECURE_SETTINGS ; " +
                            "pm grant com.zacharee1.systemuituner android.permission.DUMP ; " +
                            "pm grant com.zacharee1.systemuituner android.permission.PACKAGE_USAGE_STATS");
                    Utils.startUp(this);
                    finish();
                } else {
                    Intent intent = new Intent(this, SetupActivity.class);
                    intent.putExtra("permission_needed", ret);
                    startActivity(intent);
                    finish();
                }
            } else {
                Utils.startUp(this);
                finish();
            }
        }

        finish();
    }
}
