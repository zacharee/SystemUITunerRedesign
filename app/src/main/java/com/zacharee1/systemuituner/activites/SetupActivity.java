package com.zacharee1.systemuituner.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zacharee1.systemuituner.LauncherActivity;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.MiscStrings;
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

        boolean secureSetup = checkCallingOrSelfPermission(MiscStrings.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        boolean dumpSetup = checkCallingOrSelfPermission(MiscStrings.DUMP) == PackageManager.PERMISSION_GRANTED;
        boolean usageSetup = checkCallingOrSelfPermission(MiscStrings.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED;

        TextView comm1 = findViewById(R.id.no_root_comm_1);
        TextView comm2 = findViewById(R.id.no_root_comm_2);
        TextView comm3 = findViewById(R.id.no_root_comm_3);

        comm1.setTextColor(secureSetup ? Color.GREEN : Color.RED);
        comm2.setTextColor(dumpSetup ? Color.GREEN : Color.RED);
        comm3.setTextColor(usageSetup ? Color.GREEN : Color.RED);
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
            recreate();
        }
    }

    public void launchInstructions(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://zacharywander.tk/#sysuituner_adb")));
    }
}