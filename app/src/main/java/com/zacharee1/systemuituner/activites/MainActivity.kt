package com.zacharee1.systemuituner.activites

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.misc.OptionSelected
import com.zacharee1.systemuituner.util.BillingUtil
import com.zacharee1.systemuituner.util.prefs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_donate.*

class MainActivity : BaseAnimActivity() {
    private val billing by lazy { BillingUtil(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        open_tweaks.setOnClickListener { launchList() }
        paypal_button.setOnClickListener { onDonatePayPalClicked() }
        donate_1.setOnClickListener { onDonate1Clicked() }
        donate_2.setOnClickListener { onDonate2Clicked() }
        donate_5.setOnClickListener { onDonate5Clicked() }
        donate_10.setOnClickListener { onDonate10Clicked() }
    }

    override fun onResume() {
        super.onResume()

        if (prefs.hideWelcomeScreen) {
            launchList()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return OptionSelected.doAction(item.itemId, this)
    }

    private fun launchList() {
        startActivity(Intent(this, OptionsActivity::class.java))
    }

    private fun onDonatePayPalClicked() {
        BillingUtil.onDonatePayPalClicked(this)
    }

    private fun onDonate1Clicked() {
        billing.onDonateClicked("donate_1")
    }

    private fun onDonate2Clicked() {
        billing.onDonateClicked("donate_2")
    }

    private fun onDonate5Clicked() {
        billing.onDonateClicked("donate_5")
    }

    private fun onDonate10Clicked() {
        billing.onDonateClicked("donate_10")
    }
}
