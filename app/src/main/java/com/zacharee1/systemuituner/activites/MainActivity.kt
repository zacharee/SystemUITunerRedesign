package com.zacharee1.systemuituner.activites

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.handlers.RecreateHandler
import com.zacharee1.systemuituner.misc.OptionSelected
import com.zacharee1.systemuituner.util.BillingUtil
import com.zacharee1.systemuituner.util.Utils

class MainActivity : AppCompatActivity() {
    private var mBilling: BillingUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //        if (sharedPreferences.getBoolean("use_fabric", true))
        //            Fabric.with(this, new Crashlytics());

        setTheme(if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark else R.style.AppTheme)

        RecreateHandler.onCreate(this)

        setContentView(R.layout.activity_main)

        mBilling = BillingUtil(this)
    }

    override fun onDestroy() {
        RecreateHandler.onDestroy(this)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OptionSelected.doAction(item, this)
    }

    fun launchList(v: View?) {
        startActivity(Intent(this, ItemListActivity::class.java))
    }

    fun onDonatePayPalClicked(v: View) {
        BillingUtil.onDonatePayPalClicked(this)
    }

    fun onDonate1Clicked(v: View) {
        mBilling!!.onDonateClicked("donate_1")
    }

    fun onDonate2Clicked(v: View) {
        mBilling!!.onDonateClicked("donate_2")
    }

    fun onDonate5Clicked(v: View) {
        mBilling!!.onDonateClicked("donate_5")
    }

    fun onDonate10Clicked(v: View) {
        mBilling!!.onDonateClicked("donate_10")
    }
}
