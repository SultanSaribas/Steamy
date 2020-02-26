package com.st.steamy.data.permanent

import android.os.AsyncTask
import com.st.steamy.data.myContentResolver
import com.st.steamy.data.storage.News
import com.st.steamy.data.storage.getGame

class GetNewsFromDataBase : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        val uri = SteamyContentProvider.NEWS_URI
        val projection = arrayOf(SteamyContract.NewsEntry.COLUMN_GAME_STEAMID,
            SteamyContract.NewsEntry.COLUMN_TITLE,
            SteamyContract.NewsEntry.COLUMN_CONTENT,
            SteamyContract.NewsEntry.COLUMN_NEWS_ID)
        val cursor = myContentResolver?.query(uri, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val game = getGame(cursor.getLong(cursor.getColumnIndex(SteamyContract.NewsEntry.COLUMN_GAME_STEAMID)))
                val title = cursor.getString(cursor.getColumnIndex(SteamyContract.NewsEntry.COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndex(SteamyContract.NewsEntry.COLUMN_CONTENT))
                val newsID = cursor.getString(cursor.getColumnIndex(SteamyContract.NewsEntry.COLUMN_NEWS_ID))
                val news = News(newsID.split("/")[1].toLong())
                news.setData(title, content)
                game.addNews(news)
            }
        }
        cursor?.close()
        return 0
    }
}

fun getNewsFromDataBase() {
    GetNewsFromDataBase().execute()
}
