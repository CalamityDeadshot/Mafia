package com.app.mafia

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.app.mafia.adapters.PlayersAdapter
import com.app.mafia.helpers.*
import com.app.mafia.helpers.eventTypes.ActorSubjectEvent
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
    var submittedForVote: ArrayList<Int> = ArrayList()
    var votedOut: ArrayList<Int> = ArrayList()
    var playerKilled = false
    var votingRunning = false
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_game)
        announcementText = "${getString(R.string.day)} $daysCounter"
        super.onCreate(savedInstanceState)
        setSupportActionBar(gameToolbar)
        supportActionBar!!.title = "${getString(R.string.day)} $daysCounter"
        mainButton.setOnClickListener(this); mainButton.text = "${resources.getString(R.string.end_day)} $daysCounter"

        playersRecycler.layoutManager = GridLayoutManager(this, Global.calculateNoOfColumns(this, 120f))
        val players = ArrayList<PlayerModel>()
        val roles: ArrayList<Roles> = (intent.extras!!.getSerializable("playerRoles") as ArrayList<Roles>)
        for (i in 0 until roles.size) {
            players.add(PlayerModel(roles[i], i + 1))
        }
        adapter = PlayersAdapter(this, players, this, this)
        playersRecycler.adapter = adapter
        playersRecycler.setOnClickListener(this)
        game = Game()
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
            R.id.gotKilled -> {
                if (!playerKilled) {
                    playerKilled = true
                    game.addEvent(GameEvent(SubjectEvent.KILL, position))
                } else {
                    Toast.makeText(this, R.string.player_already_killed, Toast.LENGTH_LONG).show()
                }
            }
            R.id.regFoul -> game.addEvent((GameEvent(SubjectEvent.FOUL, position)))
            R.id.startSpeak -> {
                if (!mainButton.timerRunning) mainButton.timerRunning = true
            }
            R.id.startsVote -> {
                mainButton.waitingForConfirmation = true
            }
            R.id.votedOut -> {
                votedOut.add(position)
                game.addEvent(GameEvent(SubjectEvent.VOTE_KICK, position))
            }
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
                if (anyAnimationRunning()) return
                if (mainButton.timerRunning) return
                if (mainButton.waitingForConfirmation) {
                    confirmVote()
                    return
                }
                if (submittedForVote.size > 0) {
                    if (!votingRunning) {
                        initiateVoting()
                    } else {
                        if (!mainButton.stateSelected) {
                            mainButton.stateSelected = true
                            return
                        }
                        votingRunning = false
                        var kickedPlayers = ""
                        for (i in 0 until votedOut.size) {
                            kickedPlayers += if (i != votedOut.size - 1) "${votedOut[i]}${if (i == votedOut.size - 2) "" else ","} "
                            else "${if (votedOut.size == 1) "" else getString(R.string.and)} ${votedOut[i]}"
                        }
                        announce(if (votedOut.size > 0) "${
                            if (votedOut.size == 1) 
                                getString(R.string.player) 
                            else getString(R.string.players)
                        } $kickedPlayers ${
                            if (votedOut.size == 1) getString(R.string.voted_out_single) 
                            else getString(R.string.voted_out_plural)
                        }" else getString(R.string.no_one_voted_out), timeToRead = 2500)
                        adapter.views.forEach {
                            (it as PlayerCard).isBeingVoted = false
                            it.setEnabled(!it.kicked && !it.killed && !votedOut.contains(it.model.number))
                        }
                        submittedForVote.clear(); votedOut.clear()
                        mainButton.text = "${getString(R.string.end_day)} $daysCounter"
                        mainButton.stateSelected = false
                    }
                    return
                }
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
        playerKilled = false
        isDayNow = !isDayNow
        if (!isDayNow) daysCounter++
        if (isDayNow) {
            announce("${getString(R.string.day)} $daysCounter", THEME_LIGHT)
        } else {
            announce("${getString(R.string.night)} $daysCounter", THEME_DARK)
        }
        //announce("${if (isDayNow) "Day" else "Night"} $daysCounter", if (isDayNow) R.color.colorPrimaryLight else R.color.darkBase)
        mainButton.animate().withLayer()
            .setDuration(400)
            .alpha(1f)
            .withEndAction {
                supportActionBar!!.title = "${if (isDayNow) getString(R.string.day) else getString(R.string.night)} $daysCounter"
                mainButton.text = "${if (isDayNow) getString(R.string.end_day) else getString(R.string.end_night)} $daysCounter"
                mainButton.stateSelected = false
            }
        game.addEvent(GameEvent(if (isDayNow) Event.DAY else Event.NIGHT, daysCounter))
    }

    fun confirmVote() {
        if (!mainButton.stateSelected) {
            mainButton.stateSelected = true
            return
        }
        if (anyAnimationRunning()) return
        var submitted = -1
        adapter.views.forEach {
            if ((it as PlayerCard).isSelectedForVote) submitted = it.model.number
            it.isSelectedForVote = false
            it.leaveVotingState()
            adapter.voteRunning = false
        }
        if (submitted != -1) {
            game.addEvent(GameEvent(ActorSubjectEvent.VOTE_SUBMIT, adapter.voteInitializer, submitted))
            if (!submittedForVote.contains(submitted)) submittedForVote.add(submitted)
            mainButton.oldText = getString(R.string.proceed_to_voting)
        }
        mainButton.waitingForConfirmation = false
        mainButton.stateSelected = false
    }

    fun initiateVoting() {
        votingRunning = true
        announce(R.string.voting_begin)
        mainButton.text = getString(R.string.confirm)
        adapter.views.forEach {
            if (submittedForVote.contains((it as PlayerCard).model.number)) it.isBeingVoted = true
            else it.setEnabled(false)
        }
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("game", game)
        var listState = playersRecycler.layoutManager!!.onSaveInstanceState()
        outState.putParcelable("list", listState)
        outState.putInt("daysCounter", daysCounter)
        outState.putBoolean("isDayNow", isDayNow)
        outState.putIntegerArrayList("submittedForVote", submittedForVote)
        outState.putIntegerArrayList("votedOut", votedOut)
        outState.putBoolean("playerKilled", playerKilled)
        outState.putBoolean("votingRunning", votingRunning)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        game = savedInstanceState.getParcelable<Game>("game")!!
        playersRecycler.layoutManager!!.onRestoreInstanceState(savedInstanceState.getParcelable("list"))
        daysCounter = savedInstanceState.getInt("daysCounter")
        isDayNow = savedInstanceState.getBoolean("isDayNow")
        submittedForVote = savedInstanceState.getIntegerArrayList("submittedForVote")!!
        votedOut = savedInstanceState.getIntegerArrayList("votedOut")!!
        playerKilled = savedInstanceState.getBoolean("playerKilled")
        votingRunning = savedInstanceState.getBoolean("votingRunning")
    }*/
}