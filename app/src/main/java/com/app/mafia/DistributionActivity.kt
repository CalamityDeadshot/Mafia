package com.app.mafia

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.mafia.adapters.PlayerPlaceholderPagerAdapter
import com.app.mafia.adapters.RoleCardsAdapter
import com.app.mafia.helpers.AnimatedActivity
import com.app.mafia.helpers.ItemOffsetDecoration
import com.app.mafia.helpers.Roles
import com.app.mafia.models.DistributionItemModel
import kotlinx.android.synthetic.main.activity_distribution.*
import kotlin.random.Random


class DistributionActivity : AnimatedActivity(), Animator.AnimatorListener, AdapterView.OnItemClickListener {

    var numberOfPlayers : Int = 0
    var numberOfMafias : Int = 0
    lateinit var placeholderAdapter : PlayerPlaceholderPagerAdapter
    lateinit var roleCardsAdapter : RoleCardsAdapter
    var playerRoles : ArrayList<Roles> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_distribution)
        super.onCreate(savedInstanceState)
        numberOfPlayers = intent.getIntExtra("players", -1)
        numberOfMafias = intent.getIntExtra("mafias", -1)

        if (numberOfMafias == -1 || numberOfPlayers == -1) {
            closeActivity()
        }

        viewPager.setOnTouchListener { v, event -> true }
        placeholderAdapter = PlayerPlaceholderPagerAdapter(this, numberOfPlayers)
        viewPager.adapter = placeholderAdapter

        val availableNumbers = BooleanArray(numberOfPlayers - 1) { true }
        val mafiasNumbers = IntArray(numberOfMafias)

        for (i in 0 until numberOfMafias) {
            var rand = Random.nextInt(1, numberOfPlayers)
            while (!availableNumbers[rand - 1]) {
                rand = Random.nextInt(1, numberOfPlayers)
            }
            availableNumbers[rand - 1] = false
            mafiasNumbers[i] = rand
        }

        val don = mafiasNumbers[Random.nextInt(0, numberOfMafias - 1)]
        var comm = Random.nextInt(0, numberOfPlayers - 1)
        while (mafiasNumbers.contains(comm)) comm = Random.nextInt(0, numberOfPlayers - 1)
        //println("mafias: ${mafiasNumbers.contentToString()}, don: $don, comm: $comm")
        val data : ArrayList<DistributionItemModel> = ArrayList()
        for (i in 0 until numberOfPlayers) {
            data.add(DistributionItemModel(
                when {
                    mafiasNumbers.contains(i) -> {
                        if (i == don) Roles.DON else Roles.MAFIA
                    }
                    i == comm -> Roles.COMMISSIONER
                    else -> Roles.PEACEFUL
                }
            ))
        }

        //val rand = Random.nextInt(0, numberOfPlayers - 1)

        roleCardsAdapter = RoleCardsAdapter(this, data, this)
        playersRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //Global.calculateNoOfColumns(this, 120f)
        playersRecycler.adapter = roleCardsAdapter
        val itemDecoration =
            ItemOffsetDecoration(this, R.dimen.item_offset)
        playersRecycler.addItemDecoration(itemDecoration)

    }

    override fun onAnimationEnd(p0: Animator?) {
        when (p0) {
            shrink -> {
                val data = Intent()
                if (playerRoles.size == numberOfPlayers) {
                    data.putExtra("playerRoles", playerRoles)
                    setResult(RESULT_OK, data)
                } else setResult(RESULT_CANCELED)
            }
        }

        super<AnimatedActivity>.onAnimationEnd(p0)
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if (roleCardsAdapter.list[position].selected) {
            playerRoles.add(placeholderAdapter.list[viewPager.currentItem].role)
            if (numberOfPlayers - 1 == viewPager.currentItem) {
                closeActivity()
            } else {
                viewPager.currentItem++
                roleCardsAdapter.removeItemAt(position)
            }
        } else {
            placeholderAdapter.list[viewPager.currentItem].role = roleCardsAdapter.list[position].role
        }

    }
}