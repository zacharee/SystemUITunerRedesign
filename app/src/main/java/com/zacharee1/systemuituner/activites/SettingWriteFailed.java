package com.zacharee1.systemuituner.activites;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.zacharee1.systemuituner.R;

public class SettingWriteFailed extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_write_failed);

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
        finish();
        return true;
    }
}
