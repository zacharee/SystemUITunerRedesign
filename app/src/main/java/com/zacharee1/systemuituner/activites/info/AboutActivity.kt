package com.zacharee1.systemuituner.activites.info

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.BaseAnimActivity
import com.zacharee1.systemuituner.util.BillingUtil

class AboutActivity : BaseAnimActivity() {
    private var mBilling: BillingUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)
        setTitle(R.string.about)

        mBilling = BillingUtil(this)

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val appCred = findViewById<TextView>(R.id.app_credit)
        val langCred = findViewById<TextView>(R.id.lang_credit)

        appCred.movementMethod = LinkMovementMethod.getInstance()
        langCred.movementMethod = LinkMovementMethod.getInstance()
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
