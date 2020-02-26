package com.st.steamy.data.storage

import com.st.steamy.data.INITIALIZELOGO
import com.st.steamy.data.INITIALIZENAME
import com.st.steamy.data.permanent.addGameToDataBase
import com.st.steamy.data.privateGame

val gameCollection: MutableMap<Long, Game> = mutableMapOf()

fun addGame(id: Long, title: String, logoAddress: String) {
    val game = Game(id, title)
    game.logoAdress = logoAddress
    gameCollection[id] = game
    addGameToDataBase(game)
}

fun getGame(id: Long): Game {
    if (id==0L) {
        return privateGame!!
    }

    val res = gameCollection[id]
    if (res == null) {
        val game = Game(id, INITIALIZENAME)
        gameCollection[id] = game
        return game
    }
    return res
}

