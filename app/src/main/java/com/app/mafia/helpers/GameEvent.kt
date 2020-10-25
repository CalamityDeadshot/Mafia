package com.app.mafia.helpers

import com.app.mafia.helpers.eventTypes.ActorSubjectEvent
import com.app.mafia.helpers.eventTypes.Event
import com.app.mafia.helpers.eventTypes.SubjectEvent
import java.io.Serializable

class GameEvent: Serializable {
    private val TYPE_BASIC = 1
    private val TYPE_TIME = 2
    val EVENT_TYPE_TIME = 0
    val EVENT_TYPE_SUBJECT = 1
    val EVENT_TYPE_ACTOR_SUBJECT = 2
    var eString: String
    var eventTypeGeneral: Int = TYPE_BASIC
    var eventTypeEnum: Int
    var eventSubtypeEnum: Int

    constructor(e: Event, number: Int) {
        eventTypeEnum = EVENT_TYPE_TIME //Event
        eventSubtypeEnum = e.ordinal
        eString = when (e) {
            Event.DAY -> "Day $number has come"
            Event.NIGHT -> "Night $number has come"
        }
        eventTypeGeneral = TYPE_TIME
    }

    constructor(e: SubjectEvent, subject: Int) {
        eventTypeEnum = EVENT_TYPE_SUBJECT // SubjectEvent
        eventSubtypeEnum = e.ordinal
        eString = when (e) {
            SubjectEvent.FOUL -> "Player $subject receives a foul"
            SubjectEvent.KILL -> "Player $subject gets killed this night"
            SubjectEvent.VOTE_KICK -> "Player $subject is imprisoned by vote"
        }
        eventTypeGeneral = TYPE_BASIC
    }
    constructor(e: ActorSubjectEvent, actor: Int, subject: Int) {
        eventTypeEnum = EVENT_TYPE_ACTOR_SUBJECT // ActorSubjectEvent
        eventSubtypeEnum = e.ordinal
        eString = when (e) {
            ActorSubjectEvent.VOTE_SUBMIT -> "Player $actor submits player $subject for a vote"
        }
        eventTypeGeneral = TYPE_BASIC
    }

    fun getEvent(): Pair<Int, Int> {
        return Pair(eventTypeEnum, eventSubtypeEnum)
    }

}