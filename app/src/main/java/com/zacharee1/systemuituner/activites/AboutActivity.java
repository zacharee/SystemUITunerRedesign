package com.zacharee1.systemuituner.activites;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zacharee1.systemuituner.R;

public class AboutActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
