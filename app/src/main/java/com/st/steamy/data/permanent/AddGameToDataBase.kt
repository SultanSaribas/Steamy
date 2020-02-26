package com.st.steamy.data.permanent

import android.content.ContentResolver
import android.content.ContentValues
import android.os.AsyncTask
import com.st.steamy.data.myContentResolver
import com.st.steamy.data.storage.Game

class AddGameToDataBase(val game: Game) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        val uri = SteamyContentProvider.GAME_URI
        val value = ContentValues()
        value.put(SteamyContract.GameEntry.COLUMN_NAME, game.title)
        value.put(SteamyContract.GameEntry.COLUMN_STEAMID, game.steamId)
        value.put(SteamyContract.GameEntry.COLUMN_LOGO, game.logoAdress)
        myContentResolver?.insert(uri, value)
        return 0
    }
}

fun addGameToDataBase(game: Game) {
    AddGameToDataBase(game).execute()
}
