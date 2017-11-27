package com.zacharee1.systemuituner.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.util.BillingUtil;
import com.zacharee1.systemuituner.misc.OptionSelected;
import com.zacharee1.systemuituner.handlers.RecreateHandler;
import com.zacharee1.systemuituner.util.Utils;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity
{
    private BillingUtil mBilling;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

//        if (sharedPreferences.getBoolean("use_fabric", true))
//            Fabric.with(this, new Crashlytics());

        setTheme(Utils.isInDarkMode(this) ? R.style.AppTheme_Dark : R.style.AppTheme);

        RecreateHandler.onCreate(this);

        setContentView(R.layout.activity_main);

        mBilling = new BillingUtil(this);
    }

    @Override
    protected void onDestroy()
    {
        RecreateHandler.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return OptionSelected.doAction(item, this);
    }

    public void launchList(@Nullable View v) {
        startActivity(new Intent(this, ItemListActivity.class));
    }

    public void onDonatePayPalClicked(View v) {
        BillingUtil.onDonatePayPalClicked(this);
    }

    public void onDonate1Clicked(View v) {
        mBilling.onDonateClicked("donate_1");
    }

    public void onDonate2Clicked(View v) {
        mBilling.onDonateClicked("donate_2");
    }

    public void onDonate5Clicked(View v) {
        mBilling.onDonateClicked("donate_5");
    }

    public void onDonate10Clicked(View v) {
        mBilling.onDonateClicked("donate_10");
    }
}
