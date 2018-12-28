package com.zacharee1.systemuituner.activites.info

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.instructions.SetupActivity
import com.zacharee1.systemuituner.misc.OptionSelected
import com.zacharee1.systemuituner.util.checkPermissions
import com.zacharee1.systemuituner.util.prefs
import com.zacharee1.systemuituner.util.startUp
import com.zacharee1.systemuituner.util.sudo

class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        backButton.visibility = View.GONE
        skipButton.visibility = View.VISIBLE

        addSlide(WarningSlideFragment.newInstance(
                resources.getString(R.string.attention),
                resources.getString(R.string.warn_message),
                R.drawable.ic_warning_black_24dp,
                resources.getColor(android.R.color.holo_red_dark, null)
        ))

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.welcome),
                resources.getString(R.string.intro_1),
                R.drawable.ic_hand_black_24dp,
                resources.getColor(R.color.intro_1, null)
        ))

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.some_things),
                resources.getString(R.string.intro_2),
                R.drawable.ic_brush_black_24dp,
                resources.getColor(R.color.intro_2, null)
        ))

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.auto_detect),
                resources.getString(R.string.intro_3),
                R.drawable.ic_track_changes_black_24dp,
                resources.getColor(R.color.intro_3, null)
        ))

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.no_root),
                resources.getString(R.string.intro_4),
                R.drawable.ic_sudo_black_24dp,
                resources.getColor(R.color.intro_4, null)
        ))

        addSlide(SlideFragment.newInstance(
                resources.getString(R.string.permissions),
                resources.getString(R.string.intro_5),
                R.drawable.ic_check_black_24dp,
                resources.getColor(R.color.intro_5, null)
        ))

        setColorTransitionsEnabled(true)
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        finish()
    }

    override fun onPageSelected(position: Int) {
        val frag = if (position > 0) fragments[position - 1] else null

        if (frag is WarningSlideFragment && !frag.canGoNext()) {
            pager.goToPreviousSlide()
            skipButton.visibility = View.VISIBLE
            backButton.visibility = View.GONE
        } else {
            if (position > 0) {
                skipButton.visibility = View.GONE
                backButton.visibility = View.VISIBLE
            } else {
                skipButton.visibility = View.VISIBLE
                backButton.visibility = View.GONE
            }
        }
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        (newFragment as SlideFragment?)?.onShown()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        val perms = arrayListOf(Manifest.permission.WRITE_SECURE_SETTINGS, Manifest.permission.DUMP, Manifest.permission.PACKAGE_USAGE_STATS)

        val ret = checkPermissions(perms)

        if (ret.isNotEmpty()) {
            if (Shell.rootAccess()) {
                sudo("pm grant $packageName ${Manifest.permission.WRITE_SECURE_SETTINGS}",
                        "pm grant $packageName ${Manifest.permission.DUMP}",
                        "pm grant $packageName ${Manifest.permission.PACKAGE_USAGE_STATS}")
                startUp()
                finishAndSave()
            } else {
                SetupActivity.make(this, ret)
                finishAndSave()
            }
        } else {
            startUp()
            finishAndSave()
        }
    }

    private fun finishAndSave() {
        prefs.showIntro = false

        finish()
    }

    open class SlideFragment : Fragment(), ISlideBackgroundColorHolder {
        internal open val layoutId = R.layout.appintro_fragment_intro2

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val args = arguments
            val view = inflater.inflate(layoutId, container, false)

            val title = view.findViewById<TextView>(R.id.title)
            title.text = args!!.getString("title", "")

            val desc = view.findViewById<TextView>(R.id.description)
            desc.text = args.getString("description", "")

            val drawable = view.findViewById<ImageView>(R.id.image)
            val drawableId = args.getInt("drawableId")
            if (drawableId != 0) drawable.setImageResource(drawableId)

            val image = view?.findViewById<ImageView>(R.id.image)
            image?.setColorFilter(Color.WHITE)

            val params = image?.layoutParams
            params?.width = ViewGroup.LayoutParams.MATCH_PARENT
            params?.height = ViewGroup.LayoutParams.MATCH_PARENT

            return view
        }

        override fun setBackgroundColor(backgroundColor: Int) {
            view?.setBackgroundColor(backgroundColor)
        }

        override fun getDefaultBackgroundColor(): Int {
            return arguments!!.getInt("color")
        }

        open fun onShown() {}

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

    class WarningSlideFragment : SlideFragment() {
        override val layoutId = R.layout.slide_warning

        private val agree by lazy { view?.findViewById<CheckBox>(R.id.agree) }
        private val countdown by lazy { view?.findViewById<TextView>(R.id.countdown) }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val terms = view.findViewById<Button>(R.id.terms)
            terms.setOnClickListener {
                OptionSelected.doAction(R.id.action_terms, activity!!)
            }
        }

        fun canGoNext(): Boolean {
            return agree?.isChecked == true
        }

        override fun onShown() {
            if (agree?.isChecked == false) {
                makeCountDown()
            }
        }

        private var timer: CountDownTimer? = null

        private fun makeCountDown() {
            if (timer == null) {
                agree?.isEnabled = false
                agree?.alpha = 0.4f

                countdown?.visibility = View.VISIBLE

                timer = object : CountDownTimer(5000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        countdown?.text = (millisUntilFinished / 1000L).toString()
                    }

                    override fun onFinish() {
                        agree?.isEnabled = true
                        agree?.alpha = 1f

                        countdown?.visibility = View.GONE
                    }
                }

                countdown?.text = "5"
                timer?.start()
            }
        }

        companion object {
            fun newInstance(title: String, description: String, drawableId: Int, color: Int): SlideFragment {
                val fragment = WarningSlideFragment()
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
