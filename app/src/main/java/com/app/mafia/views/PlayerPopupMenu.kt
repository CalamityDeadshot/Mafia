package com.app.mafia.views

import android.content.Context
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.RequiresApi

class PlayerPopupMenu: PopupMenu {
    lateinit var mMenuItemClickListener : OnMenuItemClickListener

    constructor(context: Context, anchor: View, gravity: Int) : super(context, anchor, gravity)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    constructor(context: Context, anchor: View, gravity: Int, popupStyleAttr: Int, popupStyleRes: Int) : super(context, anchor, gravity, popupStyleAttr, popupStyleRes)

    fun setOnMenuItemClickListener(listener: OnMenuItemClickListener) {
        mMenuItemClickListener = listener
    }

    public interface OnMenuItemClickListener {
        fun onMenuItemClick(item: MenuItem, position: Int): Boolean
    }
}