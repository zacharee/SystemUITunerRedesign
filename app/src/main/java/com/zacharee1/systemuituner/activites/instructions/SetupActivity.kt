package com.zacharee1.systemuituner.activites.instructions

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.github.paolorotolo.appintro.AppIntro2
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.Utils
import java.util.*

class SetupActivity : AppIntro2() {
    private var permissionsNeeded: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setButtonState(backButton, false)
        showSkipButton(true)
        showPagerIndicator(false)

        supportActionBar!!.hide()

        val intent = intent

        if (intent != null) {
            permissionsNeeded = intent.getStringArrayExtra("permission_needed")

            addSlide(PermsFragment.newInstance(
                    resources.getString(R.string.permissions),
                    resources.getString(R.string.adb_setup),
                    permissionsNeeded,
                    resources.getColor(R.color.intro_1, null)
            ))
        }
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        if (permissionsNeeded != null) {
            val missing = Utils.checkPermissions(this, permissionsNeeded!!)

            if (missing.isNotEmpty()) {
                AlertDialog.Builder(this)
                        .setTitle(R.string.missing_perms)
                        .setMessage(Arrays.toString(missing))
                        .setPositiveButton(R.string.ok, null)
                        .show()
            } else {
                Utils.startUp(this)
                finish()
            }
        } else {
            Utils.startUp(this)
            finish()
        }
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        finish()
    }

    fun launchInstructions(v: View) {
        //        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://zacharywander.tk/#sysuituner_adb")));
        val intent = Intent(this, InstructionsActivity::class.java)
        intent.putStringArrayListExtra(InstructionsActivity.ARG_COMMANDS, ArrayList(Arrays.asList(*permissionsNeeded!!)))
        startActivity(intent)
    }

    class PermsFragment : Fragment() {
        private var mView: View? = null

        @SuppressLint("SetTextI18n")
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val args = arguments

            mView = inflater.inflate(R.layout.permissions_fragment, container, false)

            mView!!.setBackgroundColor(args!!.getInt("color"))
            mView!!.findViewById<View>(R.id.adb_instructions).backgroundTintList = ColorStateList.valueOf(args.getInt("color"))

            val title = mView!!.findViewById<TextView>(R.id.title)
            title.text = args.getString("title", "")

            val desc = mView!!.findViewById<TextView>(R.id.description)
            desc.text = args.getString("description", "")

            val text = mView!!.findViewById<LinearLayout>(R.id.perms_layout)
            val perms = args.getStringArray("permissions")

            if (perms != null) {
                for (perm in perms) {
                    val command = "adb shell pm grant " + activity!!.packageName + " "

                    val textView = TextView(activity)
                    textView.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    textView.setTextIsSelectable(true)
                    textView.text = command + perm
                    textView.setPadding(
                            0,
                            Utils.pxToDp(activity!!, 8f).toInt(),
                            0,
                            0
                    )

                    text.addView(textView)
                }
            }

            return mView
        }

        companion object {

            fun newInstance(title: String, description: String, permissions: Array<String>?, color: Int): PermsFragment {
                val fragment = PermsFragment()
                val args = Bundle()
                args.putString("title", title)
                args.putString("description", description)
                args.putInt("color", color)
                args.putStringArray("permissions", permissions)
                fragment.arguments = args
                return fragment
            }
        }
    }
}