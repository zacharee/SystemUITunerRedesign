package com.zacharee1.systemuituner.activites;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.utils.SettingsUtils;
import com.zacharee1.systemuituner.utils.SuUtils;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
