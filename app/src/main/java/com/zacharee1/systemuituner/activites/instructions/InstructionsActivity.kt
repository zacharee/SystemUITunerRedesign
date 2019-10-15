package com.zacharee1.systemuituner.activites.instructions

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder
import com.github.paolorotolo.appintro.ISlidePolicy
import com.zacharee1.systemuituner.R
import kotlinx.android.synthetic.main.command_box.view.*
import java.util.*

class InstructionsActivity : AppIntro2() {

    private lateinit var initial: Instructions
    private lateinit var commands: Commands
    private lateinit var instructions: OSInstructions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instructions = OSInstructions.newInstance(resources.getString(R.string.choose_your_weapon),
                resources.getString(R.string.which_os),
                R.layout.fragment_adb_select,
                resources.getColor(R.color.intro_5, null))

        initial = Instructions.newInstance(resources.getString(R.string.initial_setup),
                resources.getString(R.string.on_device),
                R.layout.fragment_adb_initial,
                resources.getColor(R.color.intro_2, null))

        val intent = intent
        if (intent == null) {
            finish()
            return
        }

        val extras = intent.extras
        if (extras == null) {
            finish()
            return
        }

        val cmds = extras.getStringArrayList(ARG_COMMANDS)

        commands = Commands.newInstance(resources.getString(R.string.run_commands),
                resources.getString(R.string.run_on_computer),
                resources.getColor(R.color.intro_6, null),
                cmds)

        addSlide(initial)
        addSlide(instructions)
        addSlide(commands)

        pager.offscreenPageLimit = 300

        backButton.visibility = View.GONE
        skipButtonEnabled = false
        doneButton.visibility = View.GONE

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

    open class OSInstructions: Instructions(), ISlidePolicy {
        companion object {
            @JvmOverloads
            fun newInstance(title: CharSequence, description: CharSequence,
                            @LayoutRes layoutId: Int, @ColorInt bgColor: Int,
                            @ColorInt titleColor: Int = 0, @ColorInt descColor: Int = 0): OSInstructions {
                val slide = OSInstructions()
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

        private val selection by lazy { findViewById<Button>(R.id.change_selection) }

        private var hasSelectedAnOs = false
        private var isOnOsSelectionScreen = true

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            addSelectorButton()
            setSelectionListeners()
        }

        override fun isPolicyRespected(): Boolean {
            return !isOnOsSelectionScreen || hasSelectedAnOs
        }

        override fun onUserIllegallyRequestedNextPage() {
            Toast.makeText(context, R.string.select_os, Toast.LENGTH_SHORT).show()
        }

        private fun setSelectionListeners() {
            selection?.visibility = View.GONE

            val windows = findViewById<RadioButton>(R.id.choose_windows)
            val mac = findViewById<RadioButton>(R.id.choose_mac)
            val ubuntu = findViewById<RadioButton>(R.id.choose_ubuntu)
            val fedora = findViewById<RadioButton>(R.id.choose_fedora)
            val linux = findViewById<RadioButton>(R.id.choose_linux)

            val clickListener = View.OnClickListener {
                var title = 0
                var layout = 0

                when (it.id) {
                    R.id.choose_windows -> {
                        title = R.string.windows_setup
                        layout = R.layout.fragment_adb_windows
                    }

                    R.id.choose_mac -> {
                        title = R.string.mac_setup
                        layout = R.layout.fragment_adb_mac
                    }

                    R.id.choose_ubuntu -> {
                        title = R.string.ubuntu_setup
                        layout = R.layout.fragment_adb_ubuntu
                    }

                    R.id.choose_fedora -> {
                        title = R.string.fedora_setup
                        layout = R.layout.fragment_adb_fedora
                    }

                    R.id.choose_linux -> {
                        title = R.string.linux_setup
                        layout = R.layout.fragment_adb_linux
                    }
                }

                animateChange(layout, title, R.string.on_computer,
                        Runnable { selection?.visibility = View.VISIBLE })
                isOnOsSelectionScreen = false
                hasSelectedAnOs = true
            }

            windows?.setOnClickListener(clickListener)
            mac?.setOnClickListener(clickListener)
            ubuntu?.setOnClickListener(clickListener)
            fedora?.setOnClickListener(clickListener)
            linux?.setOnClickListener(clickListener)
        }

        private fun addSelectorButton() {
            selection?.setOnClickListener {
                animateChange(R.layout.fragment_adb_select, R.string.choose_your_weapon, R.string.which_os, Runnable { setSelectionListeners() })
                isOnOsSelectionScreen = true
                hasSelectedAnOs = false
            }
        }

        private fun animateChange(targetLayout: Int, targetTitle: Int, targetDesc: Int, runnable: Runnable) {
            view?.findViewById<LinearLayout>(R.id.animation_dummy)?.animate()?.let {
                it.setListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator?) {
                        setInternalLayout(View.inflate(context ?: return, targetLayout, null) as ViewGroup)
                        setTitle(resources.getString(targetTitle))
                        setDescription(resources.getString(targetDesc))
                        runnable.run()

                        it.setListener(null)
                        it.alpha(1.0f).setDuration(500L).start()
                    }

                    override fun onAnimationCancel(animation: Animator?) {}

                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationStart(animation: Animator?) {}
                })

                it.alpha(0.0f).setDuration(500L).start()
            }
        }
    }

    open class Instructions : Fragment(), ISlideBackgroundColorHolder {
        private val layoutId: Int = R.layout.fragment_intro_custom_center

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(layoutId, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val main = view.findViewById<View>(R.id.main)
            val title = view.findViewById<TextView>(R.id.title)
            val desc = view.findViewById<TextView>(R.id.description)

            val args = arguments

            main.setBackgroundColor(args!!.getInt(ARG_BG_COLOR))
            title.text = args.getString(ARG_TITLE)
            desc.text = args.getString(ARG_DESC)

            val argLayout = args.getInt(ARG_LAYOUT)

            if (argLayout > 0) {
                val group = View.inflate(activity, argLayout, null) as ViewGroup
                setInternalLayout(group)
            }
        }

        fun <T : View> findViewById(@IdRes id: Int): T? {
            return view?.findViewById(id)
        }

        fun setTitle(title: String) {
            findViewById<TextView>(R.id.title)?.text = formatText(title)
        }

        fun setDescription(description: String) {
            findViewById<TextView>(R.id.description)?.text = formatText(description)
        }

        fun setInternalLayout(group: ViewGroup) {
            val holder = view?.findViewById<LinearLayout>(R.id.custom_layout_holder)
            holder?.removeAllViews()

            val viewChild = group.getChildAt(0) as ViewGroup

            for (i in 0 until viewChild.childCount) {
                val v = viewChild.getChildAt(i)

                if (v is TextView && v !is Button) {
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
            view!!.setBackgroundColor(backgroundColor)
        }

        private fun formatText(text: String): Spanned {
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) Html.fromHtml(text, 0) else HtmlCompat.fromHtml(text, 0)
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
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val commands = arguments?.getStringArrayList(ARG_COMMANDS) ?: return
            val holder = view.findViewById<LinearLayout>(R.id.custom_layout_holder)

            for (command in commands) {
                val commandBox = layoutInflater.inflate(R.layout.command_box, holder, false)

                commandBox.command.text = "adb shell pm grant ${view.context.packageName} $command"
                holder.addView(commandBox)
            }
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
