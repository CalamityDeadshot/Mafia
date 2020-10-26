package com.app.mafia.views

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.mafia.GameActivity
import com.app.mafia.R
import com.app.mafia.helpers.Global
import kotlinx.android.synthetic.main.main_game_button.view.*

class MainGameButton : ConstraintLayout, View.OnClickListener {
    var mContext: Context
    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.main_game_button, this)
        this.mContext = context
        endTimerButton.setOnClickListener(this)
    }

    var text: String? = ""
        set(value) {
            field = value
            textView.text = value
        }
    var oldText = ""
    var waitingForVoteConfirmation = false
        set(value) {
            field = value
            if (value) oldText = text!!
            textView.animate().withLayer()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    text = if (value) "Confirm" else oldText
                    textView.animate().withLayer()
                        .alpha(1f)
                        .start()
                }
                .start()
        }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        View.inflate(context, R.layout.main_game_button, this)
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MainGameButton,
            0, 0).apply {
            try {
                text = getString(R.styleable.MainGameButton_text)
            } finally {
                recycle()
            }
        }
        this.mContext = context
        endTimerButton.setOnClickListener(this)
    }

    var stateSelected = false
        set(value) {
            field = value
            if (field) {
                animCircle.animate().withLayer()
                    .scaleX(5f)
                    .scaleY(5f)
                    .setDuration(Global.shrinkExpandDuration)
                    .withEndAction {
                        tick.animate().withLayer()
                            .alpha(1f)
                            .setDuration(300)
                            .start()
                    }.start()
            } else {
                animCircle.animate().withLayer()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(Global.shrinkExpandDuration)
                    .withStartAction {
                        tick.animate().withLayer()
                            .alpha(0f)
                            .setDuration(300)
                            .start()
                    }.start()
            }
        }
    val speakingTime = 60000L
    var timer = object : CountDownTimer(speakingTime, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val time = millisUntilFinished / 1000
            timerLeft.text = "0:${if (time > 9) time else "0$time"}"
        }

        override fun onFinish() {
            timerRunning = false
        }
    }
    var timerRunning = false
        set(value) {
            val oldValue = field
            field = value
            if (value && !oldValue) {
                timerLeft.text = "0:59"
                timerOverlay.pivotX = 0f
                timerOverlay.scaleX = 1f
                timerBase.animate().withLayer()
                    .withStartAction {
                        timerBase.visibility = View.VISIBLE
                        textView.visibility = View.GONE
                        textView.alpha = 0f
                    }
                    .alpha(1f)
                    .setDuration(500)
                    .withEndAction {
                        timer = object : CountDownTimer(speakingTime, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val time = millisUntilFinished / 1000
                                timerLeft.text = "0:${if (time > 9) time else "0$time"}"
                            }

                            override fun onFinish() {
                                timerRunning = false
                            }
                        }
                        timer.start()

                        timerOverlay.animate().withLayer()
                            .scaleX(0f)
                            .setDuration(speakingTime)
                            .setInterpolator(LinearInterpolator())
                            .start()
                    }
                    .start()
            } else {
                timer.cancel()
                timerOverlay.scaleX = 1f
                timerBase.animate().withLayer()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        timerOverlay.clearAnimation()
                        timerOverlay.animate().cancel()

                        timerBase.visibility = View.GONE
                        textView.visibility = View.VISIBLE
                        textView.animate().withLayer()
                            .alpha(1f)
                            .setDuration(300)
                            .start()
                    }.start()

            }
        }

    override fun onClick(view: View?) {
        when (view!!) {
            endTimerButton -> {
                timerRunning = false
                (mContext as GameActivity).adapter.views.forEach{ (it as PlayerCard).speaking = false }
            }
        }
    }
}