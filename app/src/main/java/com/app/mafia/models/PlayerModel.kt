package com.app.mafia.models

import com.app.mafia.helpers.Roles

class PlayerModel(var role: Roles, var number: Int) {
    var fouls: Int = 0
        set(value) {
            field = value
            if (field == 4) {
                isDead = true
            }
        }
    var isDead: Boolean = false
}