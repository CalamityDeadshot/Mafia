package com.app.mafia.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.mafia.views.PlayerCardPlaceholder

class PlayerPlaceholderPagerAdapter(var context: Context, var numberOfCards: Int) : PagerAdapter() {

    var list : ArrayList<PlayerCardPlaceholder> = ArrayList()

    fun createView(position: Int): PlayerCardPlaceholder = PlayerCardPlaceholder(context, position)
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as PlayerCardPlaceholder
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var card = PlayerCardPlaceholder(context, position)
        container.addView(card)
        list.add(card)
        return card
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as PlayerCardPlaceholder)
    }

    override fun getCount(): Int {
        return numberOfCards
    }

}