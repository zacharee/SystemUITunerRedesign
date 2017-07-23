package com.zacharee1.systemuituner.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zacharee1.systemuituner.LauncherActivity;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.RecreateHandler;
import com.zacharee1.systemuituner.misc.SettingsUtils;

@SuppressWarnings("unused")
public class SetupActivity extends AppCompatActivity
{
    @SuppressWarnings("FieldCanBeLocal")
    private static boolean DARK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        DARK = sharedPreferences.getBoolean("dark_mode", false);

        setTheme(DARK ? R.style.AppTheme_Dark : R.style.AppTheme);

        RecreateHandler.onCreate(this);

        setContentView(R.layout.activity_setup);
    }

    @Override
    protected void onDestroy()
    {
        RecreateHandler.onDestroy(this);
        super.onDestroy();
    }

    public void checkSetup(View v) {
        if (SettingsUtils.hasPerms(this)) {
            Intent intent = new Intent(this, LauncherActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, getResources().getString(R.string.permissions_failed), Toast.LENGTH_SHORT).show();
        }
    }
}