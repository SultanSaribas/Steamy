package com.st.steamy.activity.abstraction

import android.os.Bundle
import com.st.steamy.data.storage.Game
import com.st.steamy.data.storage.getGame
import java.lang.Exception


var activeGame: Long? = null


abstract class GameRelatedActivity: ActivityBase() {

    var game: Game? = null

    fun getActiveGame(): Game {
        return game as Game
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(activeGame == null) {
            throw Exception("To call game related activity please initialize the active game")
        }
        game = getGame(activeGame as Long)
    }

}