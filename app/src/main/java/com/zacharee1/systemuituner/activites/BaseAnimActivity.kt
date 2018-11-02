package com.zacharee1.systemuituner.activites

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.isInDarkMode

@SuppressLint("Registered")
open class BaseAnimActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    lateinit var toolbar: Toolbar
    lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (isInDarkMode()) R.style.AppTheme_Dark else R.style.AppTheme)

        super.onCreate(savedInstanceState)

        super.setContentView(R.layout.activity_base)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

        content = findViewById(R.id.content_internal)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        toolbar.popupTheme = if (isInDarkMode()) R.style.AppTheme_PopupTheme_Dark else R.style.AppTheme_PopupTheme_Light

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setContentView(layoutResID: Int) {
        LayoutInflater.from(this).inflate(layoutResID, content, true)
    }

    override fun setContentView(view: View?) {
        setContentView(view, null)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        content.removeAllViews()
        addContentView(view, params)
    }

    override fun addContentView(view: View?, params: ViewGroup.LayoutParams?) {
        if (params != null) {
            content.addView(view, params)
        } else content.addView(view)
    }

    @CallSuper
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "dark_mode" -> recreate()
        }
    }

    fun setBackClickable(clickable: Boolean) {
        val mNavButtonView = toolbar::class.java.getDeclaredField("mNavButtonView")
        mNavButtonView.isAccessible = true

        val view = mNavButtonView.get(toolbar) as ImageButton
        view.isClickable = clickable

        view.animate()
                .alpha(if (clickable) 1.0f else 0.0f)
                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .start()
    }
}