package com.app.mafia.helpers

import android.content.Context
import android.os.Parcel
import android.os.Parcelable

class Game(): Parcelable {
    /*constructor(context: Context, players: ArrayList<Roles>) {
        this.context = context
        this.players = players
    }

    var context = Global.context
    var players = ArrayList<Roles>()*/
    var gameEvents: ArrayList<GameEvent> = ArrayList()

    fun addEvent(e: GameEvent) {
        gameEvents.add(0, e)
    }
    // Parcelable implementation
    constructor(parcel: Parcel): this() {
        this.gameEvents = parcel.readArrayList(null) as ArrayList<GameEvent>
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(gameEvents as List<*>?)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game {
            return Game(parcel)
        }

        override fun newArray(size: Int): Array<Game?> {
            return arrayOfNulls(size)
        }
    }
}