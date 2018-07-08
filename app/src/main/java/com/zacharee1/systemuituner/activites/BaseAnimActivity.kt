package com.zacharee1.systemuituner.activites

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.Utils

@SuppressLint("Registered")
open class BaseAnimActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    lateinit var textSwitcher: TextSwitcher
    lateinit var toolbar: Toolbar
    lateinit var content: LinearLayout

    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark else R.style.AppTheme)

        super.onCreate(savedInstanceState)

        super.setContentView(R.layout.activity_base)
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

        content = findViewById(R.id.content_internal)
        toolbar = findViewById(R.id.toolbar)
        textSwitcher = findViewById(R.id.text_switcher)

        setSupportActionBar(toolbar)
        hideTitleView()

        toolbar.popupTheme = if (Utils.isInDarkMode(this)) R.style.AppTheme_PopupTheme_Dark else R.style.AppTheme_PopupTheme_Light

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val outAnim = AnimationUtils.loadAnimation(this, R.anim.pop_out)
        val inAnim = AnimationUtils.loadAnimation(this, R.anim.pop_in)

        outAnim.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        inAnim.duration = outAnim.duration

        textSwitcher.outAnimation = outAnim
        textSwitcher.inAnimation = inAnim
        textSwitcher.setFactory {
            AppCompatTextView(this).apply {
                setTextAppearance(android.R.style.TextAppearance_Material_Widget_ActionBar_Title)
                setTextColor(Color.WHITE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onAttachedToWindow() {
        if (super.getTitle() != null) setTitle(super.getTitle())
    }

    override fun setTitle(titleId: Int) {
        if (titleId > 0) {
            setTitle(resources.getText(titleId))
        }
    }

    override fun setTitle(title: CharSequence?) {
        try {
            textSwitcher.setText(title)
        } catch (e: UninitializedPropertyAccessException) {}

        this.title = title?.toString()
        super.setTitle(null)
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

    private fun hideTitleView() {
        val mTitleTextView = toolbar::class.java.getDeclaredField("mTitleTextView")
        mTitleTextView.isAccessible = true

        val view = mTitleTextView.get(toolbar) as TextView
        view.visibility = View.GONE
    }
}