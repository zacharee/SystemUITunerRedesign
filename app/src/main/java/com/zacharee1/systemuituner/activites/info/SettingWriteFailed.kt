package com.zacharee1.systemuituner.activites.info

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.handlers.RecreateHandler
import com.zacharee1.systemuituner.util.Utils

class SettingWriteFailed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark else R.style.AppTheme)
        setContentView(R.layout.activity_setting_write_failed)

        RecreateHandler.onCreate(this)

        if (intent.action != null) {
            val extras = intent.extras

            if (extras != null) {
                val command = extras.getString("command")

                val textView = findViewById<TextView>(R.id.sorry_command)
                textView.text = command
            }
        }

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return true
    }

    override fun onDestroy() {
        RecreateHandler.onDestroy(this)
        super.onDestroy()
    }
}
