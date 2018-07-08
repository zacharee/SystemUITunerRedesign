package com.zacharee1.systemuituner.activites

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.misc.OptionSelected
import com.zacharee1.systemuituner.util.BillingUtil

class MainActivity : BaseAnimActivity() {
    private var billing: BillingUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        billing = BillingUtil(this)
    }

    override fun onResume() {
        super.onResume()

        val hideWelcomeScreen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hide_welcome_screen", false)
        if (hideWelcomeScreen) {
            launchList(null)
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OptionSelected.doAction(item, this)
    }

    fun launchList(v: View?) {
        startActivity(Intent(this, OptionsActivity::class.java))
    }

    fun onDonatePayPalClicked(v: View) {
        BillingUtil.onDonatePayPalClicked(this)
    }

    fun onDonate1Clicked(v: View) {
        billing!!.onDonateClicked("donate_1")
    }

    fun onDonate2Clicked(v: View) {
        billing!!.onDonateClicked("donate_2")
    }

    fun onDonate5Clicked(v: View) {
        billing!!.onDonateClicked("donate_5")
    }

    fun onDonate10Clicked(v: View) {
        billing!!.onDonateClicked("donate_10")
    }
}
