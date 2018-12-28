package com.zacharee1.systemuituner.activites.info

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.widget.TextView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.BaseAnimActivity
import com.zacharee1.systemuituner.util.BillingUtil
import kotlinx.android.synthetic.main.layout_donate.*

class AboutActivity : BaseAnimActivity() {
    private val billing by lazy { BillingUtil(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)
        setTitle(R.string.about)

        val appCred = findViewById<TextView>(R.id.app_credit)
        val langCred = findViewById<TextView>(R.id.lang_credit)

        appCred.movementMethod = LinkMovementMethod.getInstance()
        langCred.movementMethod = LinkMovementMethod.getInstance()

        paypal_button.setOnClickListener { onDonatePayPalClicked() }
        donate_1.setOnClickListener { onDonate1Clicked() }
        donate_2.setOnClickListener { onDonate2Clicked() }
        donate_5.setOnClickListener { onDonate5Clicked() }
        donate_10.setOnClickListener { onDonate10Clicked() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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
