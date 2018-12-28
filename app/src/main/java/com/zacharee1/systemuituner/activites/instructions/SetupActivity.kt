package com.zacharee1.systemuituner.activites.instructions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.checkPermissions
import com.zacharee1.systemuituner.util.pxToDp
import com.zacharee1.systemuituner.util.startUp
import kotlinx.android.synthetic.main.permissions_fragment.*

class SetupActivity : AppIntro2() {
    companion object {
        const val PERMISSION_NEEDED = "permission_needed"

        val NOT_REQUIRED = arrayListOf(
                Manifest.permission.DUMP,
                Manifest.permission.PACKAGE_USAGE_STATS
        )

        fun make(context: Context, permissions: ArrayList<String>) {
            val intent = Intent(context, SetupActivity::class.java)
            intent.putStringArrayListExtra(PERMISSION_NEEDED, permissions)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private var permissionsNeeded: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setButtonState(backButton, false)
        showSkipButton(true)
        showPagerIndicator(false)

        supportActionBar?.hide()

        val intent = intent

        if (intent != null) {
            permissionsNeeded = ArrayList(intent.getStringArrayListExtra(PERMISSION_NEEDED)?.filterNot { checkCallingOrSelfPermission(it) == PackageManager.PERMISSION_GRANTED } ?: return)

            addSlide(PermsFragment.newInstance(
                    resources.getString(R.string.permissions),
                    resources.getString(R.string.adb_setup),
                    permissionsNeeded,
                    resources.getColor(R.color.intro_1, null)
            ))
        }

        adb_instructions.setOnClickListener { launchInstructions() }
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        if (permissionsNeeded != null) {
            val missing = checkPermissions(permissionsNeeded!!)
            val requiredMissing = ArrayList(missing).apply { removeAll(NOT_REQUIRED) }
            val notRequiredMissing = ArrayList(missing).apply { removeAll(requiredMissing) }

            if (requiredMissing.isNotEmpty()) {
                AlertDialog.Builder(this)
                        .setTitle(R.string.missing_perms)
                        .setMessage(requiredMissing.toString())
                        .setPositiveButton(R.string.ok, null)
                        .show()
            } else {
                if (notRequiredMissing.isNotEmpty()) {
                    AlertDialog.Builder(this)
                            .setTitle(R.string.missing_perms)
                            .setMessage(R.string.missing_not_required_perms_desc)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                startUp()
                                finish()
                            }
                            .setNegativeButton(R.string.no, null)
                            .show()
                } else {
                    startUp()
                    finish()
                }
            }
        } else {
            startUp()
            finish()
        }
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        finish()
    }

    private fun launchInstructions() {
        if (permissionsNeeded == null) return

        val intent = Intent(this, InstructionsActivity::class.java)
        intent.putStringArrayListExtra(InstructionsActivity.ARG_COMMANDS, permissionsNeeded)
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
            val perms = args.getStringArrayList("permissions")

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
                            activity!!.pxToDp(8f).toInt(),
                            0,
                            0
                    )

                    text.addView(textView)
                }
            }

            return mView
        }

        companion object {
            fun newInstance(title: String, description: String, permissions: ArrayList<String>?, color: Int): PermsFragment {
                val fragment = PermsFragment()
                val args = Bundle()
                args.putString("title", title)
                args.putString("description", description)
                args.putInt("color", color)
                args.putStringArrayList("permissions", permissions)
                fragment.arguments = args
                return fragment
            }
        }
    }
}