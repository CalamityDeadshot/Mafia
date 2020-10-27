package com.app.mafia.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.app.mafia.GameActivity
import com.app.mafia.R
import com.app.mafia.models.PlayerModel
import com.app.mafia.views.PlayerCard
import com.app.mafia.views.PlayerPopupMenu
import kotlinx.android.synthetic.main.player_card.view.*

class PlayersAdapter : RecyclerView.Adapter<PlayersAdapter.ViewHolder> {

    val list: ArrayList<PlayerModel>
    val views: ArrayList<View> = ArrayList()
    private val inflater: LayoutInflater
    private val mContext: Context
    private var clickListener : AdapterView.OnItemClickListener
    private var menuItemClickListener: PlayerPopupMenu.OnMenuItemClickListener
    var voteRunning = false
    var voteInitializer = -1

    constructor(context: Context, data: ArrayList<PlayerModel>, itemClickListener: AdapterView.OnItemClickListener, menuItemClickListener: PlayerPopupMenu.OnMenuItemClickListener) {
        this.inflater = LayoutInflater.from(context)
        this.list = data
        this.mContext = context
        this.clickListener = itemClickListener
        this.menuItemClickListener = menuItemClickListener
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, PopupMenu.OnMenuItemClickListener {

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            itemView.showRoleButton.setOnClickListener(this)
            itemView.setOnTouchListener(this)
            (itemView as PlayerCard).popupMenuDay.setOnMenuItemClickListener(this)
            (itemView as PlayerCard).popupMenuNight.setOnMenuItemClickListener(this)
            (itemView as PlayerCard).popupMenuVote.setOnMenuItemClickListener(this)
        }

        override fun onClick(view: View) {

            when (view) {
                itemView -> {
                    if (voteRunning) {
                        if ((itemView as PlayerCard).isSelectedForVote) itemView.isSelectedForVote = false
                        else {
                            views.forEach {
                                (it as PlayerCard).isSelectedForVote = false
                            }
                            itemView.isSelectedForVote = true
                        }
                    }
                }
                itemView.showRoleButton -> {
                    /*for (i in list) {
                        if (i.showed) {
                            i.showed = false
                            notifyItemChanged(list.indexOf(i))
                            break
                        }
                    }
                    list[adapterPosition].showed = true
                    notifyItemChanged(adapterPosition)*/
                    for (i in views) { if (i != this.itemView) (i as PlayerCard).showRole = false }
                    //println((this.itemView as PlayerCard).showRole)
                    (itemView as PlayerCard).showRole = !itemView.showRole
                }
            }

            clickListener.onItemClick(null, itemView, adapterPosition, 0)
        }

        override fun onLongClick(view: View?): Boolean {
            val vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if ((itemView as PlayerCard).model.isDead) {
                Toast.makeText(mContext, "Player is dead", Toast.LENGTH_LONG).show()
                vibrator.vibrate(300)
                return false
            }
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                else -> {
                    vibrator.vibrate(100)
                }
            }
            if (!voteRunning) {
                if (!(view as PlayerCard).isBeingVoted) {
                    if (view.isDark()) view.popupMenuNight.show() else view.popupMenuDay.show()
                } else view.popupMenuVote.show()
            } else {
                if (itemView.isSelectedForVote) itemView.isSelectedForVote = false
                else {
                    views.forEach {
                        (it as PlayerCard).isSelectedForVote = false
                    }
                    itemView.isSelectedForVote = true
                }
            }

            return true
        }

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    scaleDown(view as PlayerCard)
                }
                else -> {
                    scaleUp(view as PlayerCard)
                }
            }
            return false
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            if ((itemView as PlayerCard).showRole) itemView.showRole = false
            when (item!!.itemId) {
                R.id.regFoul -> {
                    for (i in views) { if (i != this.itemView) (i as PlayerCard).showRole = false }
                    list[adapterPosition].fouls++
                    notifyItemChanged(adapterPosition)
                    /*itemView.foulsText.text = (itemView.foulsText.text.toString().toInt() + 1).toString()
                    itemView.foulsText.visibility = View.VISIBLE*/
                }
                R.id.startSpeak -> {
                    var alreadyRunning = false
                    for (i in views) {
                        alreadyRunning = (i as PlayerCard).speaking
                        i.speaking = false
                    }
                    itemView.speaking = alreadyRunning
                }
                R.id.startsVote -> {
                    voteRunning = true
                    voteInitializer = list[adapterPosition].number
                    views.forEach {
                        (it as PlayerCard).enterVotingState()
                    }
                }
                R.id.gotKilled -> {
                    try {
                        if (!(mContext as GameActivity).playerKilled) {
                            list[adapterPosition].isDead = true
                            //notifyItemChanged(adapterPosition)
                            itemView.killed = true
                        }
                    } catch (e: Exception) {}
                }
                R.id.votedOut -> {
                    itemView.kicked = true
                }
            }
            itemView.onMenuItemClick(item, list[adapterPosition].number)
            menuItemClickListener.onMenuItemClick(item, list[adapterPosition].number)
            return false
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersAdapter.ViewHolder {
        val view = inflater.inflate(R.layout.player_card_item, parent, false)

        views.add(ViewHolder(view).itemView)
        if((view as PlayerCard).isDark()) view.setDarkTheme()
        else view.setLightTheme()

        return ViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: PlayersAdapter.ViewHolder, position: Int) {
        val model = list[position]
        (holder.itemView as PlayerCard).text.text = model.number.toString()
        holder.itemView.model = model
        //println(views[position] == holder)
        views[position] = holder.itemView
        //holder.itemView.background = mContext.getDrawable(R.drawable.player_card_placeholder)
        //println(views[position] == holder)
        holder.itemView.foulsText.text = model.fouls.toString()
        holder.itemView.foulsText.visibility = if (model.fouls > 0) View.VISIBLE else View.INVISIBLE
        if (holder.itemView.isDark()) holder.itemView.setDarkTheme() else holder.itemView.setLightTheme()
        //holder.itemView.showRole = model.showed
    }

    fun scaleDown(view: View) {
        view.animate().withLayer()
            .setDuration(ViewConfiguration.getLongPressTimeout().toLong())
            .scaleY(.95f)
            .scaleX(.95f)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                scaleUp(view, 100)
            }
            .start()
    }

    fun scaleUp(view: View) {
        view.animate().withLayer()
            .setDuration(ViewConfiguration.getLongPressTimeout().toLong())
            .scaleY(1f)
            .scaleX(1f)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }

    fun scaleDown(view: View, duration: Long) {
        view.animate().withLayer()
            .setDuration(duration)
            .scaleY(.95f)
            .scaleX(.95f)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }

    fun scaleUp(view: View, duration: Long) {
        view.animate().withLayer()
            .setDuration(duration)
            .scaleY(1f)
            .scaleX(1f)
            .setInterpolator(AccelerateInterpolator())
            .start()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}