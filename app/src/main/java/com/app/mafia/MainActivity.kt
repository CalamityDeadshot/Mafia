package com.app.mafia

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.app.mafia.helpers.Global
import com.app.mafia.helpers.Roles
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList


class MainActivity : Activity(), View.OnClickListener, Animator.AnimatorListener {
    var switched = false
    lateinit var scaleDown : ObjectAnimator
    lateinit var expand : ObjectAnimator
    lateinit var shrink : ObjectAnimator
    lateinit var buttonShrink : ObjectAnimator
    lateinit var buttonExpand : ObjectAnimator
    lateinit var fadeText : ObjectAnimator
    lateinit var showText : ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Global.init(this)
        btn_startGame.setOnClickListener(this);
        numberPicker.apply {
            maxValue = 15
            minValue = 6
            wrapSelectorWheel = false
            setOnValueChangedListener {picker, oldVal, newVal ->
                mafiaPicker.value = newVal / 3
            }
        }

        mafiaPicker.apply {
            maxValue = 5
            minValue = 2
            wrapSelectorWheel = false
            setOnValueChangedListener {picker, oldVal, newVal ->
                numberPicker.value = newVal * 3
            }
        }

        scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            animCircle,
            PropertyValuesHolder.ofFloat("scaleX", 1.5f),
            PropertyValuesHolder.ofFloat("scaleY", 1.5f)
        ).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = OvershootInterpolator()
        }
        scaleDown.addListener(this)
        scaleDown.start()

        expand = ObjectAnimator.ofPropertyValuesHolder(
            animCircle,
            PropertyValuesHolder.ofFloat("scaleX", Global.scaleFactor),
            PropertyValuesHolder.ofFloat("scaleY", Global.scaleFactor)
        ).apply {
            duration = Global.shrinkExpandDuration
            interpolator = AccelerateInterpolator()
        }
        expand.addListener(this)

        shrink = ObjectAnimator.ofPropertyValuesHolder(
            animCircle,
            PropertyValuesHolder.ofFloat("scaleX", 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f)
        ).apply {
            duration = Global.shrinkExpandDuration
            interpolator = DecelerateInterpolator()
        }
        shrink.addListener(this)

        fadeText = ObjectAnimator.ofObject(
            btn_startGame,
            "textColor",
            ArgbEvaluator(),
            Color.BLACK,
            Color.TRANSPARENT
        ).apply {
            duration = Global.shrinkExpandDuration
            interpolator = LinearInterpolator()
        }
        showText = ObjectAnimator.ofObject(
            btn_startGame,
            "textColor",
            ArgbEvaluator(),
            Color.TRANSPARENT,
            Color.BLACK
        ).apply {
            duration = Global.shrinkExpandDuration
            interpolator = LinearInterpolator()
        }
        buttonShrink = ObjectAnimator.ofPropertyValuesHolder(
            btn_startGame,
            PropertyValuesHolder.ofFloat("scaleX", 0f),
            PropertyValuesHolder.ofFloat("scaleY", 0f)
        ).apply {
            duration = Global.shrinkExpandDuration
            interpolator = DecelerateInterpolator()
        }
        buttonExpand = ObjectAnimator.ofPropertyValuesHolder(
            btn_startGame,
            PropertyValuesHolder.ofFloat("scaleX", 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f)
        ).apply {
            duration = Global.shrinkExpandDuration
            interpolator = DecelerateInterpolator()
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.btn_startGame -> {
                if (shrink.isRunning || expand.isRunning) return
                //Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
                if (switched) {
                    shrink.start()
                    showText.start()
                } else {
                    expand.start()
                    fadeText.start()
                    scaleDown.pause()
                    buttonShrink.start()
                }
                switched = !switched
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("playerRoles", data!!.extras!!.getSerializable("playerRoles") as ArrayList<Roles>)
            startActivityForResult(intent, 0)
            overridePendingTransition(0, 0)
        } else {
            when (requestCode) {
                0 -> {
                    switched = false
                    shrink.start()
                    showText.start()
                    buttonExpand.start()

                }
            }
        }
    }

    override fun onAnimationRepeat(p0: Animator?) {

    }

    override fun onAnimationEnd(p0: Animator) {
        when (p0) {
            shrink -> scaleDown.start()
            expand -> {
                val intent = Intent(this, DistributionActivity::class.java)
                intent.putExtra("players", numberPicker.value)
                    .putExtra("mafias", mafiaPicker.value)
                startActivityForResult(intent, 0)
                overridePendingTransition(0, 0)
            }
        }
    }

    override fun onAnimationCancel(p0: Animator?) {

    }

    override fun onAnimationStart(p0: Animator?) {

    }

}