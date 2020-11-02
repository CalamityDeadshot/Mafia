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
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.mafia.R
import com.app.mafia.helpers.Roles
import com.app.mafia.models.PlayerModel
import kotlinx.android.synthetic.main.player_card.view.*


class PlayerCard(context: Context) : ConstraintLayout(context), PlayerPopupMenu.OnMenuItemClickListener {

    lateinit var model: PlayerModel
    var popupMenuDay: PlayerPopupMenu
    var popupMenuNight: PlayerPopupMenu
    var popupMenuVote: PlayerPopupMenu
    private val THEME_LIGHT = 0
    private val THEME_DARK = 1
    private var currentTheme = THEME_LIGHT
    var isBeingVoted = false
    var isSelectedForVote = false
        set(value) {
            field = value

            if (value) {
                animCircle.animate().withLayer()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        tick.animate().withLayer()
                            .alpha(1f)
                            .setDuration(200)
                            .start()
                    }
                    .start()
            } else {
                animCircle.animate().withLayer()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        tick.animate().withLayer()
                            .alpha(0f)
                            .setDuration(200)
                            .start()
                    }
                    .start()

                /*tick.animate().withLayer()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        animCircle.animate().withLayer()
                            .scaleX(0f)
                            .scaleY(0f)
                            .setDuration(200)
                            .setInterpolator(DecelerateInterpolator())
                            .start()
                    }
                    .start()*/
            }
        }
    var kicked = false
        set(value) {
            field = value

            if (value) {
                cage.animate().withLayer()
                    .scaleY(1.05f)
                    .setDuration(1500)
                    .setInterpolator(BounceInterpolator())
                    .withEndAction {
                        setEnabled(false)
                    }
                    .start()
            }
        }
    var killed = false
        set(value) {
            field = value
            if (value) {
                sight.animate().withLayer()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(700)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction {
                        setEnabled(false)
                    }
                    .start()
            }

        }
    var pulse: ObjectAnimator
    init {
        var wrapper: Context = ContextThemeWrapper(context, R.style.PopupMenuLight)
        popupMenuDay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PlayerPopupMenu(wrapper, this, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0)
        } else {
            PlayerPopupMenu(wrapper, this, Gravity.NO_GRAVITY)
        }
        popupMenuDay.setOnMenuItemClickListener(this)
        (context as Activity).menuInflater.inflate(R.menu.player_card_menu_day, popupMenuDay.menu)

        wrapper = ContextThemeWrapper(context, R.style.PopupMenuDark)
        popupMenuNight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PlayerPopupMenu(wrapper, this, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0)
        } else {
            PlayerPopupMenu(wrapper, this, Gravity.NO_GRAVITY)
        }
        popupMenuNight.setOnMenuItemClickListener(this)
        context.menuInflater.inflate(R.menu.player_card_menu_night, popupMenuNight.menu)

        popupMenuVote = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            PlayerPopupMenu(context, this, Gravity.NO_GRAVITY, R.attr.actionDropDownStyle, 0)
        } else {
            PlayerPopupMenu(context, this, Gravity.NO_GRAVITY)
        }
        popupMenuVote.setOnMenuItemClickListener(this)
        context.menuInflater.inflate(R.menu.player_card_menu_voting, popupMenuVote.menu)

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
                                Roles.PEACEFUL -> resources.getString(R.string.peaceful)
                                Roles.COMMISSIONER -> resources.getString(R.string.commissioner)
                                Roles.DON -> resources.getString(R.string.don)
                                Roles.MAFIA -> resources.getString(R.string.mafia)
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
        base.clipToOutline = true
        cage.pivotY = 0f
        this.setEnabled(true)
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

    fun enterVotingState() {
        checkboxContainer.animate().withLayer()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .start()
    }

    fun leaveVotingState() {
        isSelectedForVote = false
        checkboxContainer.animate().withLayer()
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(200)
            .start()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        base.animate().withLayer()
            .alpha(if (enabled) 1f else .5f)
            .setDuration(500)
            .start()
    }

    fun isDark() = currentTheme == THEME_DARK

}