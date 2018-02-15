package com.zacharee1.systemuituner.activites.info

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.handlers.RecreateHandler
import com.zacharee1.systemuituner.util.BillingUtil
import com.zacharee1.systemuituner.util.Utils

class AboutActivity : AppCompatActivity() {
    private var mBilling: BillingUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark else R.style.AppTheme)

        RecreateHandler.onCreate(this)

        setContentView(R.layout.activity_about)

        mBilling = BillingUtil(this)

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val appCred = findViewById<TextView>(R.id.app_credit)
        val appSpanishCred = findViewById<TextView>(R.id.spanish_lang_credit)
        val appChineseCred = findViewById<TextView>(R.id.chinese_lang_credit)

        appCred.movementMethod = LinkMovementMethod.getInstance()
        appSpanishCred.movementMethod = LinkMovementMethod.getInstance()
        appChineseCred.movementMethod = LinkMovementMethod.getInstance()
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

    override fun onDestroy() {
        RecreateHandler.onDestroy(this)
        super.onDestroy()
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
