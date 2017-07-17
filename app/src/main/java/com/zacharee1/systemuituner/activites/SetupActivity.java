package com.zacharee1.systemuituner.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.utils.SettingsUtils;
import com.zacharee1.systemuituner.utils.SuUtils;

public class SetupActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
    }

    public void checkSetup(View v) {
        if (SettingsUtils.hasPerms(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, getResources().getString(R.string.permissions_failed), Toast.LENGTH_SHORT).show();
        }
    }
}