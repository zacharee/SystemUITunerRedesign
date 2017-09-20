package com.zacharee1.systemuituner.activites;

import android.Manifest;
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
import com.zacharee1.systemuituner.misc.RecreateHandler;
import com.zacharee1.systemuituner.misc.SettingsUtils;

import java.util.Arrays;

@SuppressWarnings("unused")
public class SetupActivity extends AppCompatActivity
{
    @SuppressWarnings("FieldCanBeLocal")
    private static boolean DARK = false;
    private String[] permissionsNeeded;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null) {
            permissionsNeeded = intent.getStringArrayExtra("permission_needed");

            if (permissionsNeeded != null && permissionsNeeded.length > 0) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                DARK = sharedPreferences.getBoolean("dark_mode", false);

                setTheme(DARK ? R.style.AppTheme_Dark : R.style.AppTheme);

                RecreateHandler.onCreate(this);

                setContentView(R.layout.activity_setup);

                String prefix = "adb shell pm grant com.zacharee1.systemuituner ";

                TextView comm1 = findViewById(R.id.no_root_comm_1);
                comm1.setText(prefix.concat(permissionsNeeded[0]));
                comm1.setTextColor(SettingsUtils.hasSpecificPerm(this, permissionsNeeded[0]) ? Color.GREEN : Color.RED);

                if (permissionsNeeded.length > 1) {
                    TextView comm2 = findViewById(R.id.no_root_comm_2);
                    comm2.setVisibility(View.VISIBLE);
                    comm2.setText(prefix.concat(permissionsNeeded[1]));
                    comm2.setTextColor(SettingsUtils.hasSpecificPerm(this, permissionsNeeded[1]) ? Color.GREEN : Color.RED);
                }
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        RecreateHandler.onDestroy(this);
        super.onDestroy();
    }

    public void checkSetup(View v) {
        if (SettingsUtils.hasSpecificPerms(this, permissionsNeeded)) {
            if (Arrays.asList(permissionsNeeded).contains(Manifest.permission.WRITE_SECURE_SETTINGS)) {
                Intent intent = new Intent(this, LauncherActivity.class);
                startActivity(intent);
            }

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