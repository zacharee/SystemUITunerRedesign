package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.handlers.ImmersiveHandler
import com.zacharee1.systemuituner.views.ImmersiveModeSelectorHolder

class ImmersiveModePreference : Preference {
    private val delegate = ImmersiveModePreferenceDelegate(this)

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        layoutResource = R.layout.immersive_mode_preference
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        delegate.onBind(holder)
    }

    fun update() {
        notifyChanged()
    }

    class ImmersiveModePreferenceDelegate(private val pref: ImmersiveModePreference) {
        private val listener = RadioGroup.OnCheckedChangeListener { _, checkedId ->
            val newMode = when (checkedId) {
                R.id.immersive_full -> ImmersiveHandler.FULL
                R.id.immersive_status -> ImmersiveHandler.STATUS
                R.id.immersive_nav -> ImmersiveHandler.NAV
                R.id.immersive_preconf -> ImmersiveHandler.PRECONF
                else -> ImmersiveHandler.DISABLED
            }

            pref.callChangeListener(newMode)
        }

        private var oldGroup: RadioGroup? = null

        fun onBind(holder: PreferenceViewHolder) {
            oldGroup?.setOnCheckedChangeListener(null)

            oldGroup = holder.itemView as ImmersiveModeSelectorHolder
            oldGroup?.setOnCheckedChangeListener(listener)

            run {
                val toCheck = when (ImmersiveHandler.getMode(pref.context)) {
                    ImmersiveHandler.FULL -> R.id.immersive_full
                    ImmersiveHandler.STATUS -> R.id.immersive_status
                    ImmersiveHandler.NAV -> R.id.immersive_nav
                    ImmersiveHandler.PRECONF -> R.id.immersive_preconf
                    else -> R.id.immersive_none
                }

                oldGroup?.check(toCheck)
            }
        }
    }
}