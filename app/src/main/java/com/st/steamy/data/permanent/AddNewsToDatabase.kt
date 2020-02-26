package com.st.steamy.data.permanent

import android.content.ContentValues
import android.os.AsyncTask
import com.st.steamy.data.myContentResolver
import com.st.steamy.data.storage.Game

class AddNewsToDatabase(val game: Game) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        val uri = SteamyContentProvider.NEWS_URI
        for (news in game.news) {
            val value = ContentValues()
            value.put(SteamyContract.NewsEntry.COLUMN_NEWS_ID, "${game.steamId}/${news.id}")
            value.put(SteamyContract.NewsEntry.COLUMN_GAME_STEAMID, game.steamId)
            value.put(SteamyContract.NewsEntry.COLUMN_TITLE, news.title)
            value.put(SteamyContract.NewsEntry.COLUMN_CONTENT, news.content)
            myContentResolver?.insert(uri, value)
        }
        return 0
    }
}

fun addNewsToDatabase(game: Game) {
    AddNewsToDatabase(game).execute()
}
