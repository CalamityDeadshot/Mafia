package com.app.mafia

import android.animation.Animator
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.app.mafia.adapters.PlayersAdapter
import com.app.mafia.helpers.*
import com.app.mafia.helpers.eventTypes.Event
import com.app.mafia.helpers.eventTypes.SubjectEvent
import com.app.mafia.models.PlayerModel
import com.app.mafia.views.PlayerCard
import com.app.mafia.views.PlayerPopupMenu
import kotlinx.android.synthetic.main.activity_distribution.playersRecycler
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.main_game_button.view.*

class GameActivity : AnimatedActivity(), Animator.AnimatorListener, AdapterView.OnItemClickListener, PlayerPopupMenu.OnMenuItemClickListener, View.OnClickListener {

    lateinit var game: Game
    lateinit var adapter: PlayersAdapter
    var daysCounter = 0
    var isDayNow = true
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_game)
        announcementText = "Day 0"
        super.onCreate(savedInstanceState)
        setSupportActionBar(gameToolbar)
        supportActionBar!!.title = "Day $daysCounter"
        mainButton.setOnClickListener(this)

        playersRecycler.layoutManager = GridLayoutManager(this, Global.calculateNoOfColumns(this, 120f))
        val players = ArrayList<PlayerModel>()
        val roles: ArrayList<Roles> = (intent.extras!!.getSerializable("playerRoles") as ArrayList<Roles>)
        for (i in 0 until roles.size) {
            players.add(PlayerModel(roles[i], i + 1))
        }
        adapter = PlayersAdapter(this, players, this, this)
        playersRecycler.adapter = adapter
        playersRecycler.setOnClickListener(this)
        game = Game(this, roles)
        game.addEvent(GameEvent(Event.DAY, 0))
    }

    override fun onAnimationEnd(p0: Animator?) {
        when (p0) {
            shrink -> {
                mainButton.base.background = resources.getDrawable(
                    if (currentTheme == THEME_LIGHT) R.drawable.main_button_background_light
                    else R.drawable.main_button_background_dark
                )
                adapter.views.forEach{
                    (it as PlayerCard).showRole = false
                    if (currentTheme == THEME_LIGHT) it.setLightTheme()
                    else it.setDarkTheme()
                }
                //adapter.notifyDataSetChanged()
            }
        }
        super<AnimatedActivity>.onAnimationEnd(p0)
    }

    override fun onItemClick(p0: AdapterView<*>?, view: View?, pos: Int, id: Long) {

    }

    override fun onMenuItemClick(item: MenuItem, position: Int): Boolean {
        when (item.itemId) {
            R.id.gotKilled -> game.addEvent(GameEvent(SubjectEvent.KILL, position))
            R.id.regFoul -> game.addEvent((GameEvent(SubjectEvent.FOUL, position)))
            R.id.startSpeak -> mainButton.timerRunning = true
        }

        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(application).inflate(R.menu.game_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.events_list -> {
                val intent = Intent(this, EventsListActivity::class.java)
                intent.putExtra("events", game.gameEvents)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mainButton.stateSelected) {
            mainButton.stateSelected = false
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onClick(view: View?) {
        when (view) {
            mainButton -> {
                if (mainButton.timerRunning) return
                nextTimeCycle()
            }
            playersRecycler -> mainButton.stateSelected = false
        }
    }

    fun nextTimeCycle() {
        if (!mainButton.stateSelected) {
            mainButton.stateSelected = true
            return
        }
        if (anyAnimationRunning()) return
        isDayNow = !isDayNow
        if (!isDayNow) daysCounter++
        if (isDayNow) {
            announce("Day $daysCounter", THEME_LIGHT)
        } else {
            announce("Night $daysCounter", THEME_DARK)
        }
        //announce("${if (isDayNow) "Day" else "Night"} $daysCounter", if (isDayNow) R.color.colorPrimaryLight else R.color.darkBase)
        mainButton.animate().withLayer()
            .setDuration(400)
            .alpha(1f)
            .withEndAction {
                supportActionBar!!.title = "${if (isDayNow) "Day" else "Night"} $daysCounter"
                mainButton.text = "End ${(if (isDayNow) "Day" else "Night").toLowerCase()} $daysCounter"
                mainButton.stateSelected = false
            }
        game.addEvent(GameEvent(if (isDayNow) Event.DAY else Event.NIGHT, daysCounter))
    }
}