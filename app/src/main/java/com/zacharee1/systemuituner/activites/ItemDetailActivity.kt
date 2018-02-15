package com.zacharee1.systemuituner.activites

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import com.zacharee1.systemuituner.handlers.RecreateHandler
import com.zacharee1.systemuituner.misc.OptionSelected
import com.zacharee1.systemuituner.util.Utils

class ItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark_NoActionBar else R.style.AppTheme_NoActionBar)

        RecreateHandler.onCreate(this)

        setContentView(R.layout.activity_item_detail)
        val toolbar = findViewById<Toolbar>(R.id.detail_toolbar)
        toolbar.popupTheme = if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark_PopupOverlay else R.style.AppTheme_PopupOverlay
        setSupportActionBar(toolbar)

        // Show the Up button in the action bar.
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val arguments = Bundle()
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    intent.getStringExtra(ItemDetailFragment.ARG_ITEM_ID))
            val fragment = ItemDetailFragment()
            fragment.arguments = arguments
            fragmentManager.beginTransaction()
                    .add(R.id.item_detail_container_small, fragment)
                    .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            finish()
            return true
        }

        return OptionSelected.doAction(item, this)
    }

    override fun onDestroy() {
        RecreateHandler.onDestroy(this)
        super.onDestroy()
    }
}
