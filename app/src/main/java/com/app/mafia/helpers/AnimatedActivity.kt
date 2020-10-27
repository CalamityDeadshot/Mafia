package com.app.mafia.helpers

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.mafia.R

open class AnimatedActivity : AppCompatActivity(), Animator.AnimatorListener {

    lateinit var expand : ObjectAnimator
    lateinit var shrink : ObjectAnimator
    lateinit var fadeIn : ObjectAnimator
    lateinit var fadeOut : ObjectAnimator
    lateinit var announcementFadeIn : ObjectAnimator
    //lateinit var announcementFadeOut : ObjectAnimator
    val THEME_LIGHT = 0
    val THEME_DARK = 1
    var currentTheme = THEME_LIGHT
    var baseColor = R.color.colorPrimaryLight
    private var hasAnnouncement = false
    private var activityToBeClosed = false
    private lateinit var announcement : TextView
    var announcementText = ""
        set(value) {
            field = value
            if (!hasAnnouncement) {
                hasAnnouncement = true
                return
            }
            announcement.text = value
            activityToBeClosed = false
            fadeOut.start()
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasAnnouncement) {
            announcement = findViewById(R.id.announcement)
            announcement.text = announcementText
        }
        handleAnimation()
    }

    private fun handleAnimation() {

        announcementFadeIn = ObjectAnimator.ofPropertyValuesHolder(
            this.findViewById<View>(R.id.announcement),
            PropertyValuesHolder.ofFloat("alpha", 1f)
        ).apply {
            duration = 500
            interpolator = AccelerateInterpolator()
        }
        announcementFadeIn.addListener(this)

        /*announcementFadeOut = ObjectAnimator.ofPropertyValuesHolder(
            this.findViewById<View>(R.id.announcement),
            PropertyValuesHolder.ofFloat("alpha", 0f)
        ).apply {
            duration = 700
            interpolator = AccelerateInterpolator()
        }
        announcementFadeOut.addListener(this)*/

        if (hasAnnouncement) announcementFadeIn.start()

        expand = ObjectAnimator.ofPropertyValuesHolder(
            this.findViewById<View>(R.id.bubble),
            PropertyValuesHolder.ofFloat("scaleX", Global.scaleFactor),
            PropertyValuesHolder.ofFloat("scaleY", Global.scaleFactor)
        ).apply {
            duration = Global.shrinkExpandDuration
            interpolator = AccelerateDecelerateInterpolator()
        }
        expand.addListener(this)
        if (!hasAnnouncement) expand.start()

        shrink = ObjectAnimator.ofPropertyValuesHolder(
            this.findViewById<View>(R.id.bubble),
            PropertyValuesHolder.ofFloat("scaleX", 0f),
            PropertyValuesHolder.ofFloat("scaleY", 0f)
        ).apply {
            duration = Global.shrinkExpandDuration
            interpolator = AccelerateDecelerateInterpolator()
        }
        shrink.addListener(this)

        fadeIn = ObjectAnimator.ofPropertyValuesHolder(
            this.findViewById<View>(R.id.opacityContainer),
            PropertyValuesHolder.ofFloat("alpha", 1f)
        ).apply {
            duration = 300
            interpolator = AccelerateInterpolator()
        }
        fadeIn.start()
        fadeIn.addListener(this)

        fadeOut = ObjectAnimator.ofPropertyValuesHolder(
            this.findViewById<View>(R.id.opacityContainer),
            PropertyValuesHolder.ofFloat("alpha", 0f)
        ).apply {
            duration = 300
            interpolator = AccelerateInterpolator()
        }
        fadeOut.addListener(this)
    }

    var lastPressedTime: Long = 0
    var PERIOD = 2000
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (event!!.keyCode == KeyEvent.KEYCODE_BACK) {
            when (event.action) {
                KeyEvent.ACTION_DOWN -> {
                    if (event.downTime - lastPressedTime < PERIOD) {
                        closeActivity()
                    } else {
                        Toast.makeText(this, "Press again to close the game", Toast.LENGTH_LONG).show()
                        lastPressedTime = event.eventTime
                    }
                }
            }
        }
        return false
    }

    fun closeActivity() {
        activityToBeClosed = true
        fadeOut.start()
    }

    override fun onAnimationRepeat(p0: Animator?) {
    }

    // Announcement: fadeOut -> shrink -> expand -> fadeIn
    override fun onAnimationEnd(p0: Animator?) {
        when (p0) {
            announcementFadeIn -> {
                announcement.animate().withLayer()
                    .alpha(1f)
                    .setDuration(800)
                    .withEndAction {
                        expand.start()
                    }
            }
            expand -> {
                if (currentTheme == THEME_LIGHT) this.findViewById<View>(R.id.base).background = null
                else                             this.findViewById<View>(R.id.base).setBackgroundColor(resources.getColor(R.color.darkBase))

                this.findViewById<View>(R.id.bubble).visibility = View.GONE
                this.findViewById<View>(R.id.opacityContainer).visibility = View.VISIBLE
                if (hasAnnouncement) announcement.visibility = View.GONE
                fadeIn.start()
            }
            shrink -> {
                if (activityToBeClosed) {
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    announcement.animate().withLayer()
                        .alpha(1f)
                        .setDuration(1000)
                        .withEndAction {
                            this.findViewById<View>(R.id.bubble).background = resources.getDrawable(if (currentTheme == THEME_LIGHT) R.drawable.pulse_circle_light else R.drawable.pulse_circle_dark)
                            supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(if (currentTheme == THEME_LIGHT) R.color.colorPrimary else R.color.dark900)))
                            expand.start()
                        }
                }
            }
            fadeOut -> {
                this.findViewById<View>(R.id.bubble).visibility = View.VISIBLE
                this.findViewById<View>(R.id.base).setBackgroundColor(resources.getColor(baseColor))
                if (hasAnnouncement && !activityToBeClosed) {
                    announcement.visibility = View.VISIBLE
                    announcement.alpha = 1f
                }
                shrink.start()
            }
        }
    }

    override fun onAnimationCancel(p0: Animator?) {
    }

    override fun onAnimationStart(p0: Animator?) {
    }

    fun anyAnimationRunning(): Boolean {
        return expand.isRunning || shrink.isRunning || fadeIn.isRunning || fadeOut.isRunning || announcementFadeIn.isRunning || announcement.visibility == View.VISIBLE
    }

    fun announce(text: String, theme: Int = THEME_LIGHT) {
        announcementText = text
        currentTheme = theme
        this.baseColor = if (theme == THEME_LIGHT) R.color.colorPrimaryLight else R.color.dark800
    }
}