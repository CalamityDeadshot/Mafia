package com.app.mafia.views

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.mafia.R
import com.app.mafia.helpers.Roles
import com.app.mafia.models.PlayerModel
import kotlinx.android.synthetic.main.player_card.view.*

class PlayerCard(context: Context) : ConstraintLayout(context), PlayerPopupMenu.OnMenuItemClickListener {

    lateinit var model: PlayerModel
    var popupMenu: PlayerPopupMenu
    private val THEME_LIGHT = 0
    private val THEME_DARK = 1
    private var currentTheme = THEME_LIGHT
    var pulse: ObjectAnimator
    init {
        popupMenu = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PlayerPopupMenu(this.context, this, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0)
        } else {
            PlayerPopupMenu(this.context, this, Gravity.NO_GRAVITY)
        }
        popupMenu.setOnMenuItemClickListener(this)
        (context as Activity).menuInflater.inflate(R.menu.player_card_menu, popupMenu.menu)
        pulse = ObjectAnimator.ofPropertyValuesHolder(
            this,
            PropertyValuesHolder.ofFloat("scaleX", .95f),
            PropertyValuesHolder.ofFloat("scaleY", .95f)
        ).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            duration = 500
        }
    }

    var showRole = false
        @SuppressLint("UseCompatLoadingForDrawables")
        set(value) {
            val oldValue = field
            field = value
            if (value == oldValue) return
            this.animate().withLayer()
                .rotationY(90f)
                .setDuration(300)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    run {
                        if (value) {
                            base.background = context.getDrawable(
                                when (model.role) {
                                    Roles.PEACEFUL -> R.drawable.card_peaceful
                                    Roles.COMMISSIONER -> R.drawable.card_commissioner
                                    Roles.DON -> R.drawable.card_don
                                    Roles.MAFIA -> R.drawable.card_mafia
                                    Roles.EMPTY -> R.drawable.player_card_placeholder
                                }
                            )
                            text.text = when (model.role) {
                                Roles.PEACEFUL -> "P"
                                Roles.COMMISSIONER -> "C"
                                Roles.DON -> "D"
                                Roles.MAFIA -> "M"
                                Roles.EMPTY -> model.number.toString()
                            }
                            text.setTextColor(
                                resources.getColor(
                                    when (model.role) {
                                        Roles.EMPTY -> R.color.black_overlay
                                        Roles.PEACEFUL -> R.color.black_overlay
                                        else -> android.R.color.white
                                    }
                                )
                            )
                            showRoleButton.setImageResource(android.R.drawable.ic_menu_revert)
                            foulsText.visibility = View.INVISIBLE
                        } else {
                            if (currentTheme == THEME_LIGHT) {
                                base.background =
                                    context.getDrawable(R.drawable.player_card_background_light)
                                text.setTextColor(resources.getColor(R.color.black_overlay))
                                foulsText.setTextColor(resources.getColor(R.color.black_overlay))
                            } else {
                                base.background =
                                    context.getDrawable(R.drawable.player_card_background_dark)
                                text.setTextColor(resources.getColor(android.R.color.white))
                                foulsText.setTextColor(resources.getColor(android.R.color.white))
                            }
                            showRoleButton.setImageResource(android.R.drawable.ic_menu_view)
                            text.text = model.number.toString()
                            foulsText.visibility = if (model.fouls > 0) View.VISIBLE else View.INVISIBLE
                        }

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

    var speaking = false
        set(value) {
            field = value

            if (value) pulse.start()
            else {
                pulse.end()
                this.scaleX = 1f
                this.scaleY = 1f
            }
        }

    constructor(context: Context, attrs : AttributeSet) : this(context) {
        initView()
    }

    fun initView() {
        View.inflate(context, R.layout.player_card, this)
    }

    override fun onMenuItemClick(item: MenuItem, position: Int): Boolean {
        return false
    }

    fun setDarkTheme() {
        base.background = context.getDrawable(R.drawable.player_card_background_dark)
        text.setTextColor(resources.getColor(android.R.color.white))
        foulsText.setTextColor(resources.getColor(android.R.color.white))
        currentTheme = THEME_DARK
    }

    fun setLightTheme() {
        base.background = context.getDrawable(R.drawable.player_card_background_light)
        text.setTextColor(resources.getColor(R.color.black_overlay))
        foulsText.setTextColor(resources.getColor(R.color.black_overlay))
        currentTheme = THEME_LIGHT
    }

    fun evaluateBackground() {
        this.background = context.getDrawable(R.drawable.player_card_placeholder)
    }

    fun isDark() = currentTheme == THEME_DARK

}