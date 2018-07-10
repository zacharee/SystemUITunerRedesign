package com.zacharee1.systemuituner.tasker.activities

import android.content.Context
import android.os.Bundle
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutput
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.BaseAnimActivity
import com.zacharee1.systemuituner.fragments.AnimFragment

abstract class BaseEditActivity<Input : Any> : BaseAnimActivity(), TaskerPluginConfig<Input> {
    companion object {
        private const val PREFS_RES = "res"
    }

    override val context: Context
        get() = this

    abstract val helper: TaskerPluginConfigHelperNoOutput<out Input, out TaskerPluginRunnerActionNoOutput<out Input>>
    abstract val prefsRes: Int

    internal val fragment = ConfigFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper.onCreate()

        setContentView(R.layout.activity_item_list)

        val args = Bundle()
        args.putInt(PREFS_RES, prefsRes)
        fragment.arguments = args
        fragment.setOnFinishAnimationListener {
            onFinishFragmentAttach()
        }

        fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_main, fragment, title)
                ?.commit()
    }

    override fun onBackPressed() {
        helper.finishForTasker()
    }

    open fun onFinishFragmentAttach() {}

    class ConfigFragment : AnimFragment() {
        private var listener: (() -> Unit)? = null

        override fun onAnimationFinishedEnter(enter: Boolean) {
            addPreferencesFromResource(arguments.getInt(PREFS_RES))

            listener?.invoke()
        }

        fun setOnFinishAnimationListener(listener: () -> Unit) {
            this.listener = listener
        }
    }
}