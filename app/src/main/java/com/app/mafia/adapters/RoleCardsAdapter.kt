package com.app.mafia.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.mafia.R
import com.app.mafia.models.DistributionItemModel
import com.app.mafia.views.DistributionCard
import java.lang.Exception


class RoleCardsAdapter : RecyclerView.Adapter<RoleCardsAdapter.ViewHolder> {
    val list: ArrayList<DistributionItemModel>
    private val mInflater: LayoutInflater
    private val mContext: Context
    private lateinit var clickListener : AdapterView.OnItemClickListener
    //private val mClickListener: ItemClickListener = null

    constructor(context: Context, data: ArrayList<DistributionItemModel>) {
        this.mInflater = LayoutInflater.from(context)
        this.list = data
        this.mContext = context
    }

    constructor(context: Context, data: ArrayList<DistributionItemModel>, clickListener: AdapterView.OnItemClickListener) {
        this.mInflater = LayoutInflater.from(context)
        this.list = data
        this.mContext = context
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoleCardsAdapter.ViewHolder {
        val view = mInflater.inflate(R.layout.distribution_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoleCardsAdapter.ViewHolder, position: Int) {
        holder.playerNumber.text = (position + 1).toString()
        (holder.itemView as DistributionCard).stateSelected = list[position].selected
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var playerNumber: TextView = itemView.findViewById(R.id.playerNumber)
        var base: ConstraintLayout = itemView.findViewById(R.id.base)
        override fun onClick(view: View) {
            try {
                clickListener.onItemClick(null, itemView, adapterPosition, 0)
                if (!list[adapterPosition].selected) {
                    for (i in list) i.selected = false
                    list[adapterPosition].selected = true
                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    fun removeItemAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(0, list.size)
    }

}