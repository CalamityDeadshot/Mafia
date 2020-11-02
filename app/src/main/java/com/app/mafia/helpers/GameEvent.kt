package com.app.mafia.helpers

import com.app.mafia.R
import com.app.mafia.helpers.eventTypes.ActorSubjectEvent
import com.app.mafia.helpers.eventTypes.Event
import com.app.mafia.helpers.eventTypes.SubjectEvent
import java.io.Serializable
import java.util.*

class GameEvent: Serializable {
    private val TYPE_BASIC = 1
    private val TYPE_TIME = 2
    val EVENT_TYPE_TIME = 0
    val EVENT_TYPE_SUBJECT = 1
    val EVENT_TYPE_ACTOR_SUBJECT = 2
    var eString: String
    var time = "${Calendar.getInstance().get(Calendar.HOUR_OF_DAY)}:${Calendar.getInstance().get(Calendar.MINUTE)}"
    var eventTypeGeneral: Int = TYPE_BASIC
    var eventTypeEnum: Int
    var eventSubtypeEnum: Int

    constructor(e: Event, number: Int) {
        eventTypeEnum = EVENT_TYPE_TIME //Event
        eventSubtypeEnum = e.ordinal
        eString = when (e) {
            Event.DAY -> Global.context.resources.getQuantityString(R.plurals.day_has_come, 1, number)//"Day $number has come"
            Event.NIGHT -> Global.context.resources.getQuantityString(R.plurals.night_has_come, 1, number)//"Night $number has come"
        }
        eventTypeGeneral = TYPE_TIME
    }

    constructor(e: SubjectEvent, subject: Int) {
        eventTypeEnum = EVENT_TYPE_SUBJECT // SubjectEvent
        eventSubtypeEnum = e.ordinal
        eString = when (e) {
            SubjectEvent.FOUL ->      "${Global.context.getString(R.string.player)} $subject ${Global.context.getString(R.string.receives_foul)}"
            SubjectEvent.KILL ->      "${Global.context.getString(R.string.player)} $subject ${Global.context.getString(R.string.gets_killed_this_night)}"
            SubjectEvent.VOTE_KICK -> "${Global.context.getString(R.string.player)} $subject ${Global.context.getString(R.string.gets_imprisoned)}"
        }

        eventTypeGeneral = TYPE_BASIC
    }
    constructor(e: ActorSubjectEvent, actor: Int, subject: Int) {
        eventTypeEnum = EVENT_TYPE_ACTOR_SUBJECT // ActorSubjectEvent
        eventSubtypeEnum = e.ordinal
        eString = when (e) {
            ActorSubjectEvent.VOTE_SUBMIT -> if (actor == subject) "${Global.context.getString(R.string.player)} $actor ${Global.context.getString(R.string.submits_self)}"
                                             else Global.context.resources.getQuantityString(R.plurals.submits, 1, actor, subject)
        }
        eventTypeGeneral = TYPE_BASIC
    }

    fun getEvent(): Pair<Int, Int> {
        return Pair(eventTypeEnum, eventSubtypeEnum)
    }

}