package com.zacharee1.systemuituner.activites.instructions

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.Utils
import java.util.*

class InstructionsActivity : AppIntro2() {

    private var mSelector: Instructions? = null
    private var mInitial: Instructions? = null
    private var mCommands: Commands? = null

    private var mInstructions: Instructions? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_Intro)
        super.onCreate(savedInstanceState)

        mInstructions = Instructions.newInstance(resources.getString(R.string.windows_setup),
                resources.getString(R.string.on_computer),
                R.layout.fragment_adb_windows,
                resources.getColor(R.color.intro_1, null))

        mSelector = Instructions.newInstance(resources.getString(R.string.choose_your_weapon),
                resources.getString(R.string.which_os),
                R.layout.fragment_adb_select,
                resources.getColor(R.color.intro_5, null))

        mInitial = Instructions.newInstance(resources.getString(R.string.initial_setup),
                resources.getString(R.string.on_device),
                R.layout.fragment_adb_initial,
                resources.getColor(R.color.intro_2, null))

        val intent = intent
        val extras = intent.extras
        val cmds = extras!!.getStringArrayList(ARG_COMMANDS)

        mCommands = Commands.newInstance(resources.getString(R.string.run_commands),
                resources.getString(R.string.run_on_computer),
                resources.getColor(R.color.intro_4, null),
                cmds)

        addSlide(mSelector!!)
        addSlide(mInitial!!)
        addSlide(mInstructions!!)
        addSlide(mCommands!!)

        pager.offscreenPageLimit = 300

        backButton.visibility = View.GONE
        skipButtonEnabled = false
        doneButton.visibility = View.GONE
        //        showPagerIndicator(false);

        setColorTransitionsEnabled(true)
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        val newIndex = fragments.indexOf(newFragment)

        if (newIndex == fragments.size - 1 || newIndex == -1) {
            nextButton.visibility = View.GONE
            doneButton.visibility = View.VISIBLE
        } else if (newIndex > 0) {
            backButton.visibility = View.VISIBLE
            nextButton.visibility = View.VISIBLE
            doneButton.visibility = View.GONE
        } else {
            backButton.visibility = View.GONE
            doneButton.visibility = View.GONE
            nextButton.visibility = View.VISIBLE
        }
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        finish()
    }

    private fun replaceContentById(@LayoutRes layout: Int) {
        val group = View.inflate(this, layout, null) as ViewGroup
        mInstructions!!.setInternalLayout(group)
    }

    private fun setInstructionsTitle(title: String) {
        mInstructions!!.setTitle(title)
    }

    fun chooseWindows(v: View) {
        replaceContentById(R.layout.fragment_adb_windows)
        setInstructionsTitle(resources.getString(R.string.windows_setup))
    }

    fun chooseMac(v: View) {
        replaceContentById(R.layout.fragment_adb_mac)
        setInstructionsTitle(resources.getString(R.string.mac_setup))
    }

    fun chooseUbuntu(v: View) {
        replaceContentById(R.layout.fragment_adb_ubuntu)
        setInstructionsTitle(resources.getString(R.string.ubuntu_setup))
    }

    fun chooseLinux(v: View) {
        replaceContentById(R.layout.fragment_adb_linux)
        setInstructionsTitle(resources.getString(R.string.linux_setup))
    }

    fun chooseFedora(v: View) {
        replaceContentById(R.layout.fragment_adb_fedora)
        setInstructionsTitle(resources.getString(R.string.fedora_setup))
    }

    open class Instructions : Fragment(), ISlideBackgroundColorHolder {
        private var mView: View? = null

        private val layoutId: Int
            get() = R.layout.fragment_intro_custom_center

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            mView = inflater.inflate(layoutId, container, false)

            val main = mView!!.findViewById<View>(R.id.main)
            val title = mView!!.findViewById<TextView>(R.id.title)
            val desc = mView!!.findViewById<TextView>(R.id.description)

            val args = arguments

            main.setBackgroundColor(args!!.getInt(ARG_BG_COLOR))
            title.text = args.getString(ARG_TITLE)
            desc.text = args.getString(ARG_DESC)

            if (args.getInt(ARG_LAYOUT) != 0 && args.getInt(ARG_LAYOUT) != -1) {
                val group = View.inflate(activity, args.getInt(ARG_LAYOUT), null) as ViewGroup
                setInternalLayout(group)
            }

            return mView
        }

        fun <T : View> findViewById(@IdRes id: Int): T {
            return mView!!.findViewById(id)
        }

        fun setTitle(title: String) {
            val textView = mView?.findViewById<TextView>(R.id.title)
            textView?.text = formatText(title)
        }

        fun setInternalLayout(group: ViewGroup) {
            val holder = mView?.findViewById<LinearLayout>(R.id.custom_layout_holder)
            holder?.removeAllViews()

            val viewChild = group.getChildAt(0) as ViewGroup

            for (i in 0 until viewChild.childCount) {
                val v = viewChild.getChildAt(i)

                if (v is TextView) {
                    v.text = formatText(v.text.toString())
                    v.linksClickable = true
                    v.movementMethod = LinkMovementMethod.getInstance()
                    v.setLinkTextColor(resources.getColorStateList(R.color.white, null))
                    v.setTextColor(resources.getColorStateList(R.color.white, null))
                }
            }

            holder?.addView(group)
        }

        override fun getDefaultBackgroundColor(): Int {
            return arguments!!.getInt(ARG_BG_COLOR)
        }

        override fun setBackgroundColor(backgroundColor: Int) {
            mView!!.setBackgroundColor(backgroundColor)
        }

        private fun formatText(text: String): Spanned {
            return Html.fromHtml(text)
        }

        companion object {

            @JvmOverloads
            fun newInstance(title: CharSequence, description: CharSequence,
                            @LayoutRes layoutId: Int, @ColorInt bgColor: Int,
                            @ColorInt titleColor: Int = 0, @ColorInt descColor: Int = 0): Instructions {
                val slide = Instructions()
                val args = Bundle()
                args.putString(ARG_TITLE, title.toString())
                args.putString(ARG_DESC, description.toString())
                args.putInt(ARG_LAYOUT, layoutId)
                args.putInt(ARG_BG_COLOR, bgColor)
                args.putInt(ARG_TITLE_COLOR, titleColor)
                args.putInt(ARG_DESC_COLOR, descColor)
                slide.arguments = args

                return slide
            }
        }
    }

    class Commands : Instructions() {

        @SuppressLint("SetTextI18n")
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = super.onCreateView(inflater, container, savedInstanceState)

            val commands = arguments!!.getStringArrayList(ARG_COMMANDS)
            val holder = view!!.findViewById<LinearLayout>(R.id.custom_layout_holder)

            for (command in commands!!) {
                val textView = TextView(activity)
                textView.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                textView.setTextIsSelectable(true)
                textView.text = PREFIX + command
                textView.setPadding(
                        0,
                        Utils.pxToDp(activity!!, 8f).toInt(),
                        0,
                        0
                )

                holder.addView(textView)
            }

            return view
        }

        companion object {
            fun newInstance(title: CharSequence, description: CharSequence,
                            @ColorInt bgColor: Int,
                            commands: ArrayList<String>?): Commands {
                return newInstance(title, description, bgColor, 0, 0, commands)
            }

            private fun newInstance(title: CharSequence, description: CharSequence,
                                    @ColorInt bgColor: Int,
                                    @ColorInt titleColor: Int, @ColorInt descColor: Int,
                                    commands: ArrayList<String>?): Commands {
                val slide = Commands()
                val args = Bundle()
                args.putString(ARG_TITLE, title.toString())
                args.putString(ARG_DESC, description.toString())
                args.putInt(ARG_BG_COLOR, bgColor)
                args.putInt(ARG_TITLE_COLOR, titleColor)
                args.putInt(ARG_DESC_COLOR, descColor)
                args.putStringArrayList(ARG_COMMANDS, commands)
                slide.arguments = args

                return slide
            }
        }
    }

    companion object {
        const val ARG_TITLE = "title"
        const val ARG_DESC = "desc"
        const val ARG_LAYOUT = "layout"
        const val ARG_BG_COLOR = "bg_color"
        const val ARG_TITLE_COLOR = "title_color"
        const val ARG_DESC_COLOR = "desc_color"
        const val ARG_COMMANDS = "commands"

        private const val PREFIX = "adb shell pm grant com.zacharee1.systemuituner "
    }
}
