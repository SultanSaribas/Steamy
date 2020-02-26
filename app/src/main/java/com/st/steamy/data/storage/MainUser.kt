package com.st.steamy.data.storage

import androidx.recyclerview.widget.RecyclerView
import com.st.steamy.MainProfileActivity
import com.st.steamy.data.api.getFriendList

class MainUser(val steamId: Long) {
    val friends: MutableList<Long>
    val followedGames: MutableList<Long>
    var willNotifyFriend: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    var willNotifyGame: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null
    init {
        followedGames = mutableListOf()
        friends = mutableListOf()
    }


    fun follow(id: Long) {
        friends.add(id)
    }


    fun followGame(id: Long) {
        followedGames.add(id)
    }
}