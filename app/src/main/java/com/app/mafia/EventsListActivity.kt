package com.app.mafia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.mafia.adapters.EventsAdapter
import com.app.mafia.helpers.AnimatedActivity
import com.app.mafia.helpers.GameEvent
import com.app.mafia.helpers.eventTypes.ActorSubjectEvent
import com.app.mafia.helpers.eventTypes.Event
import kotlinx.android.synthetic.main.activity_events_list.*

class EventsListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_list)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        recyclerView.adapter = EventsAdapter(this, intent.extras!!.getSerializable("events") as ArrayList<GameEvent>)

    }
}