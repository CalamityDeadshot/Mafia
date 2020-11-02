package com.app.mafia.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.mafia.R
import com.app.mafia.helpers.GameEvent
import com.app.mafia.helpers.eventTypes.ActorSubjectEvent
import com.app.mafia.helpers.eventTypes.Event
import com.app.mafia.helpers.eventTypes.SubjectEvent
import kotlinx.android.synthetic.main.event_time.view.*

class EventsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {
    val list: ArrayList<GameEvent>
    private val mInflater: LayoutInflater
    private val mContext: Context

    val TYPE_BASIC = 1
    val TYPE_TIME = 2
    val EVENT_TYPE_TIME = 0
    val EVENT_TYPE_SUBJECT = 1
    val EVENT_TYPE_ACTOR_SUBJECT = 2

    constructor(context: Context, data: ArrayList<GameEvent>) {
        this.mInflater = LayoutInflater.from(context)
        this.list = data
        this.mContext = context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == TYPE_BASIC) {
            val view = mInflater.inflate(R.layout.event_basic, parent, false)
            return BasicViewHolder(view)
        }
        val view = mInflater.inflate(R.layout.event_time, parent, false)
        return TimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = list[position]
        if (getItemViewType(position) == TYPE_BASIC) {
            (holder as BasicViewHolder).eventText.text = event.eString
            holder.eventIcon.setImageResource(when (event.getEvent().first) {
                EVENT_TYPE_SUBJECT -> {
                    when (SubjectEvent.values()[event.getEvent().second]) {
                        SubjectEvent.KILL -> R.drawable.ic_sight
                        SubjectEvent.FOUL -> R.drawable.ic_foul
                        SubjectEvent.VOTE_KICK -> R.drawable.ic_kick
                    }
                }
                EVENT_TYPE_ACTOR_SUBJECT -> {
                    when (ActorSubjectEvent.values()[event.getEvent().second]) {
                        ActorSubjectEvent.VOTE_SUBMIT -> R.drawable.ic_vote
                    }
                }
                else -> android.R.drawable.ic_menu_view
            })
        } else {
            (holder as TimeViewHolder).eventText.text = event.eString
            val value: Event = Event.values()[event.getEvent().second]
            holder.eventIcon.setImageResource(when(value) {
                Event.DAY -> R.drawable.ic_sun
                Event.NIGHT -> R.drawable.ic_moon
            })
        }
        holder.itemView.timeText.text = event.time
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class BasicViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var eventIcon: ImageView
        var eventText: TextView
        var timeText: TextView
        init {
            eventIcon = itemView.event_icon
            eventText = itemView.event_text
            timeText = itemView.timeText
        }
    }

    inner class TimeViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var eventIcon: ImageView
        var eventText: TextView
        var timeText: TextView
        init {
            eventIcon = itemView.event_icon
            eventText = itemView.event_text
            timeText = itemView.timeText
        }
    }


    override fun getItemViewType(position: Int): Int {
        return list[position].eventTypeGeneral
    }
}