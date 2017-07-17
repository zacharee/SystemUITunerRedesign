package com.zacharee1.systemuituner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
