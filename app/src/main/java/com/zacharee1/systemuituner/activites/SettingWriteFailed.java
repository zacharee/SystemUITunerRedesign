package com.zacharee1.systemuituner.activites;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.RecreateHandler;

public class SettingWriteFailed extends AppCompatActivity
{

    private static boolean DARK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        DARK = sharedPreferences.getBoolean("dark_mode", false);

        setTheme(DARK ? R.style.AppTheme_Dark : R.style.AppTheme);
        setContentView(R.layout.activity_setting_write_failed);

        RecreateHandler.onCreate(this);

        if (getIntent().getAction() != null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                String command = extras.getString("command");

                TextView textView = findViewById(R.id.sorry_command);
                textView.setText(command);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }

    @Override
    protected void onDestroy()
    {
        RecreateHandler.onDestroy(this);
        super.onDestroy();
    }
}
