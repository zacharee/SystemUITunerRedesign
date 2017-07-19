package com.zacharee1.systemuituner.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.utils.RecreateHandler;
import com.zacharee1.systemuituner.utils.SettingsUtils;
import com.zacharee1.systemuituner.utils.SuUtils;

public class MainActivity extends AppCompatActivity
{

    private static boolean DARK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        DARK = sharedPreferences.getBoolean("dark_mode", false);

        setTheme(DARK ? R.style.AppTheme_Dark : R.style.AppTheme);

        RecreateHandler.onCreate(this);

        setContentView(R.layout.activity_main);

        if (!SettingsUtils.hasPerms(this)) {
            if (SuUtils.testSudo()) {
                SuUtils.sudo("pm grant com.zacharee1.systemuituner android.permission.WRITE_SECURE_SETTINGS ; pm grant com.zacharee1.systemuituner android.permission.DUMP");
            } else {
                Intent intent = new Intent(this, SetupActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        RecreateHandler.onDestroy(this);
        super.onDestroy();
    }

    public void launchList(View v) {
        startActivity(new Intent(this, ItemListActivity.class));
    }

    public void onDonatePayPalClicked(View v) {

    }

    public void onDonate1Clicked(View v) {

    }

    public void onDonate2Clicked(View v) {

    }

    public void onDonate5Clicked(View v) {

    }

    public void onDonate10Clicked(View v) {

    }
}
