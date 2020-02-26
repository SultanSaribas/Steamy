package com.st.steamy.data.storage

import android.graphics.drawable.Drawable


class User(val steamId: Long, var name: String, var visibility: String) {
    val friends: MutableList<Long> = mutableListOf()
    val games: MutableList<Long> = mutableListOf()
    var drawable: Drawable? = null
    var photoLink: String? = null

    fun addFriend(user: Long) {
        friends.add(user)
    }

    fun addFriend(user: Iterable<Long>) {
        friends.addAll(user)
    }

    fun addGame(game: Long) {
        games.add(game)
    }

    fun addGame(game: Iterable<Long>) {
        games.addAll(game)
    }

}