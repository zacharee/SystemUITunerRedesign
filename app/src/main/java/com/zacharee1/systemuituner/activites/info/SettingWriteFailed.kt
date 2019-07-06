package com.zacharee1.systemuituner.activites.info

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.BaseAnimActivity

class SettingWriteFailed : BaseAnimActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setting_write_failed)

        if (intent.action != null) {
            val extras = intent.extras

            if (extras != null) {
                val command = extras.getString("command")

                val textView = findViewById<TextView>(R.id.command)
                textView.text = command
            }
        }
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
}
