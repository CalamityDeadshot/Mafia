package com.app.mafia.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.mafia.R
import com.app.mafia.helpers.Global
import kotlinx.android.synthetic.main.distribution_card.view.*

class DistributionCard(context: Context) : ConstraintLayout(context) {

    var stateSelected = false
        set(value) {
            field = value
            if (field) {
                animCircle.animate().withLayer()
                    .scaleX(2.5f)
                    .scaleY(2.5f)
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

    constructor(context: Context, attrs : AttributeSet) : this(context) {
        initView()
    }

    fun initView() {
        View.inflate(context, R.layout.distribution_card, this)

    }
}