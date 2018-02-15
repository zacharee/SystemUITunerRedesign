package com.zacharee1.systemuituner.activites.instructions

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.zacharee1.systemuituner.R

class TaskerInstructionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasker_instruction)

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

        return super.onOptionsItemSelected(item)
    }
}
