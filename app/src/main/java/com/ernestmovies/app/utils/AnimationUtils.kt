// ============================================
// FILE: AnimationUtils.kt
// LOCATION: Ernest-Movies-app/app/src/main/java/com/ernestmovies/app/utils/AnimationUtils.kt
// ============================================
package com.ernestmovies.app.utils

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation

object AnimationUtils {
    
    fun fadeIn(view: View, duration: Long = 300) {
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }
        view.startAnimation(fadeIn)
        view.visibility = View.VISIBLE
    }
    
    fun fadeOut(view: View, duration: Long = 300) {
        val fadeOut = AlphaAnimation(1f, 0f).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    view.visibility = View.GONE
                }
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        view.startAnimation(fadeOut)
    }
    
    fun scaleIn(view: View, duration: Long = 300) {
        val scaleIn = ScaleAnimation(
            0.5f, 1f,
            0.5f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }
        view.startAnimation(scaleIn)
        view.visibility = View.VISIBLE
    }
    
    fun pulse(view: View) {
        val pulse = ScaleAnimation(
            1f, 1.1f,
            1f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 200
            repeatCount = 1
            repeatMode = Animation.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }
        view.startAnimation(pulse)
    }
}