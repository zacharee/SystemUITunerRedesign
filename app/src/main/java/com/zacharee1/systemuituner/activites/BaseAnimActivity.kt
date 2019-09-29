package com.zacharee1.systemuituner.activites

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextSwitcher
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.shape.MaterialShapeDrawable
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.prefs

@SuppressLint("Registered")
open class BaseAnimActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    internal val toolbar by lazy { findViewById<BottomAppBar>(R.id.toolbar) }
    internal val content by lazy { findViewById<LinearLayout>(R.id.content_internal) }
    internal val titleSwitcher by lazy { findViewById<TextSwitcher>(R.id.title) }
    internal val backButton by lazy { createBackButton() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (prefs.darkMode) R.style.AppTheme_Dark else R.style.AppTheme)

        super.onCreate(savedInstanceState)

        super.setContentView(R.layout.activity_base)
        prefs.registerOnSharedPreferenceChangeListener(this)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val animDuration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        titleSwitcher.inAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in).apply { duration =  animDuration}
        titleSwitcher.outAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out).apply { duration = animDuration }

        if (prefs.darkMode) {
            with(toolbar.background as MaterialShapeDrawable) {
                val color = ElevationOverlayProvider(this@BaseAnimActivity)
                        .compositeOverlayWithThemeSurfaceColorIfNeeded(elevation)

                window.navigationBarColor = color
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
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

    override fun setTitle(titleId: Int) {
        if (titleId > 0) {
            title = resources.getText(titleId)
        }
    }

    override fun setTitle(title: CharSequence?) {
        titleSwitcher.setText(title)
        super.setTitle(null)
    }

    @CallSuper
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PrefManager.DARK_MODE -> recreate()
        }
    }

    fun setBackClickable(clickable: Boolean) {
        backButton.isClickable = clickable

        backButton.animate()
                .scaleX(if (clickable) 1f else 0f)
                .scaleY(if (clickable) 1f else 0f)
                .setInterpolator(if (clickable) OvershootInterpolator() else AnticipateInterpolator())
                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .start()
    }

    private fun createBackButton(): ImageButton {
        val mNavButtonView = Toolbar::class.java.getDeclaredField("mNavButtonView")
        mNavButtonView.isAccessible = true

        return mNavButtonView.get(toolbar) as ImageButton
    }
}