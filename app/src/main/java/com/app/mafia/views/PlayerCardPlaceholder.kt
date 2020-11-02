package com.app.mafia.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.app.mafia.R
import com.app.mafia.helpers.Roles
import kotlinx.android.synthetic.main.player_card_placeholder.view.*

class PlayerCardPlaceholder(context: Context, var number: Int) : ConstraintLayout(context) {

    init {
        initView(number)
    }

    var role: Roles = Roles.PEACEFUL
        set(value) {
            field = value
            this.animate().withLayer()
                .rotationY(90f)
                .setDuration(300)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    run {

                        imageView.setImageResource(when (value) {
                            Roles.PEACEFUL -> R.drawable.card_peaceful
                            Roles.COMMISSIONER -> R.drawable.card_commissioner
                            Roles.DON -> R.drawable.card_don
                            Roles.MAFIA -> R.drawable.card_mafia
                            Roles.EMPTY -> R.drawable.player_card_placeholder
                        })
                        text.text = when (value) {
                            Roles.PEACEFUL -> resources.getString(R.string.peaceful)
                            Roles.COMMISSIONER -> resources.getString(R.string.commissioner)
                            Roles.DON -> resources.getString(R.string.don)
                            Roles.MAFIA -> resources.getString(R.string.mafia)
                            Roles.EMPTY -> (number + 1).toString()
                        }
                        playerText.visibility = when (value) {
                            Roles.EMPTY -> View.VISIBLE
                            else -> View.GONE
                        }
                        text.setTextColor(resources.getColor(when(value) {
                            Roles.EMPTY -> R.color.black_overlay
                            Roles.PEACEFUL -> R.color.black_overlay
                            else -> android.R.color.white
                        }))

                        // second quarter turn
                        this.rotationY = -90f
                        this.animate().withLayer()
                            .rotationY(0f)
                            .setDuration(300)
                            .setInterpolator(DecelerateInterpolator())
                            .start()
                    }
                }.start()
        }


    fun initView() {
        View.inflate(context, R.layout.player_card_placeholder, this)
    }

    fun initView(number: Int) {
        View.inflate(context, R.layout.player_card_placeholder, this)
        text.text = (number + 1).toString()
    }
}