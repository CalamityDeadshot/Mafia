package com.app.mafia.helpers

import android.content.Context
import android.widget.Toast

class Game(val context: Context, var players: ArrayList<Roles>) {
    init {

    }

    val gameEvents: ArrayList<GameEvent> = ArrayList()

    fun addEvent(e: GameEvent) {
        gameEvents.add(0, e)
        //Toast.makeText(context, e.eString, Toast.LENGTH_LONG).show()
    }
}