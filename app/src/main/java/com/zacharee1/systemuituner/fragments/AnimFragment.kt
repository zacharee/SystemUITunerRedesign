package com.zacharee1.systemuituner.fragments

import android.animation.Animator
import android.animation.AnimatorInflater
import android.preference.PreferenceFragment
import com.zacharee1.systemuituner.R

open class AnimFragment : PreferenceFragment() {
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        onAnimationCreated(enter)

        val res = if (nextAnim > 0) nextAnim else if (enter) R.animator.pop_in else R.animator.pop_out
        val anim = AnimatorInflater.loadAnimator(activity, res)

        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                onAnimationFinished(enter)
            }
        })

        return anim
    }

    internal open fun onAnimationCreated(enter: Boolean) {}

    internal open fun onAnimationFinished(enter: Boolean) {}
}