package com.zacharee1.systemuituner.activites.info

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.instructions.SetupActivity
import com.zacharee1.systemuituner.util.SuUtils
import com.zacharee1.systemuituner.util.Utils

class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        backButton.visibility = View.GONE
        skipButton.visibility = View.VISIBLE

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.welcome),
                resources.getString(R.string.intro_1),
                0,
                resources.getColor(R.color.intro_1, null)
        ))

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.some_things),
                resources.getString(R.string.intro_2),
                0,
                resources.getColor(R.color.intro_2, null)
        ))

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.auto_detect),
                resources.getString(R.string.intro_3),
                0,
                resources.getColor(R.color.intro_3, null)
        ))

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.no_root),
                resources.getString(R.string.intro_4),
                0,
                resources.getColor(R.color.intro_4, null)
        ))

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.permissions),
                resources.getString(R.string.intro_5),
                0,
                resources.getColor(R.color.intro_5, null)
        ))

        setColorTransitionsEnabled(true)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        val index = slides.indexOf(newFragment)

        if (index > 0) {
            skipButton.visibility = View.GONE
            backButton.visibility = View.VISIBLE
        } else {
            skipButton.visibility = View.VISIBLE
            backButton.visibility = View.GONE
        }
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        val perms = arrayOf(Manifest.permission.WRITE_SECURE_SETTINGS, Manifest.permission.DUMP, Manifest.permission.PACKAGE_USAGE_STATS)

        val ret = Utils.checkPermissions(this, perms)

        if (ret.isNotEmpty()) {
            if (SuUtils.testSudo()) {
                SuUtils.sudo("pm grant com.zacharee1.systemuituner ${Manifest.permission.WRITE_SECURE_SETTINGS} ; " +
                        "pm grant com.zacharee1.systemuituner ${Manifest.permission.DUMP} ; " +
                        "pm grant com.zacharee1.systemuituner ${Manifest.permission.PACKAGE_USAGE_STATS}")
                Utils.startUp(this)
                finishAndSave()
            } else {
                val intent = Intent(this, SetupActivity::class.java)
                intent.putExtra("permission_needed", ret)
                startActivity(intent)
                finishAndSave()
            }
        } else {
            Utils.startUp(this)
            finishAndSave()
        }
    }

    private fun finishAndSave() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit().putBoolean("show_intro", false).apply()

        finish()
    }

    class SlideFragment : Fragment(), ISlideBackgroundColorHolder {
        private var mView: View? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val args = arguments

            mView = inflater.inflate(R.layout.fragment_intro2, container, false)

            val title = mView!!.findViewById<TextView>(R.id.title)
            title.text = args!!.getString("title", "")

            val desc = mView!!.findViewById<TextView>(R.id.description)
            desc.text = args.getString("description", "")

            val drawable = mView!!.findViewById<ImageView>(R.id.image)
            val drawableId = args.getInt("drawableId")
            if (drawableId != 0) drawable.setImageResource(drawableId)

            return mView
        }

        override fun setBackgroundColor(backgroundColor: Int) {
            mView!!.setBackgroundColor(backgroundColor)
        }

        override fun getDefaultBackgroundColor(): Int {
            return arguments!!.getInt("color")
        }

        companion object {

            fun newInstance(title: String, description: String, drawableId: Int, color: Int): SlideFragment {
                val fragment = SlideFragment()
                val args = Bundle()
                args.putString("title", title)
                args.putString("description", description)
                args.putInt("color", color)
                args.putInt("drawableId", drawableId)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
