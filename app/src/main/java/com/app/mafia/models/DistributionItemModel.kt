package com.app.mafia.models

import com.app.mafia.helpers.Roles

class DistributionItemModel(role: Roles) {
    var role : Roles = role
        get() = field
        set(newRole) {
            field = newRole
        }

    var selected : Boolean = false

}