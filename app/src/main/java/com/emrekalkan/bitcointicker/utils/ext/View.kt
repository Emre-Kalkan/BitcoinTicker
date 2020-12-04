package com.emrekalkan.bitcointicker.utils.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import com.google.android.material.textfield.TextInputLayout

fun View.setVisible(isVisible: Boolean, goneEnabled: Boolean = true) {
    visibility = when {
        isVisible -> View.VISIBLE
        goneEnabled -> View.GONE
        else -> View.INVISIBLE
    }
}

fun View.fadeAnimate(pendingAlpha: Float, duration: Long = 500, onComplete: (() -> Unit)? = null) {
    val targetAlpha = when {
        pendingAlpha > 1f -> 1f
        pendingAlpha < 0f -> 0f
        else -> pendingAlpha
    }
    alpha = if (targetAlpha == 1f) 0f else 1f
    setVisible(targetAlpha == 1f)
    animate().apply {
        this.duration = duration
        alpha(targetAlpha)
        onComplete?.let {
            setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    it()
                }
            })
        }
        start()
    }
}

fun TextInputLayout.setError(isValid: Boolean, message: String) {
    error = when (isValid) {
        true -> null
        false -> message
    }
}