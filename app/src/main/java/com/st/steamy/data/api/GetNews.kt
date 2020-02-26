package com.st.steamy.data.api

import android.os.AsyncTask
import android.util.Log
import com.st.steamy.data.permanent.addNewsToDatabase
import com.st.steamy.data.storage.Game
import com.st.steamy.data.storage.News
import com.st.steamy.data.storage.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class GameNewsTaker(val game: Game, val gameNewsNotifier: gameNewsNotifier) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        var urlConnection: HttpURLConnection? = null

        var reader: BufferedReader? = null
        var newsListJson: String? = null

        var quitEarly = false
        try {
            val url = URL(String.format("http://api.steampowered.com/ISteamNews/GetNewsForApp/v0002/?appid=%d&count=15&maxlength=8000&format=json", game.steamId))
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            val inputStream = urlConnection.inputStream

            if (inputStream != null) {
                reader = BufferedReader(InputStreamReader(inputStream))
                newsListJson = reader.readLines().joinToString(separator = "\n")
            }
        } catch (e: IOException) {
            Log.e("talha", "Error Message " + e.message)
            e.printStackTrace()
            quitEarly = true
        } finally {
            urlConnection?.disconnect()
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e("talha", "Error Message " + e.message)
                    e.printStackTrace()
                }

            }
        }

        if (quitEarly) return 1

        try {
            val newsListArray = JSONObject(newsListJson!!).getJSONObject("appnews").getJSONArray("newsitems")


            for (i in 0 until newsListArray.length()) {
                val news = newsListArray.getJSONObject(i)
                val newsObject = News(news.getLong("gid"))
                newsObject.setData(news.getString("title"), news.getString("contents"))
                game.addNews(newsObject)
            }

        } catch (e: org.json.JSONException) {
            Log.e("talha", "Error Message " + e.message)
            e.printStackTrace()
            Log.e("talha", newsListJson ?:"Not Found string")
            Log.v("talha", "Json Fail Printing Console")
            quitEarly = true
        }

        return if (quitEarly) 2 else 0
    }

    override fun onPostExecute(bla: Int?) {
        super.onPostExecute(bla)
        when (bla) {
            1 -> Log.e("talha", "Internet Exception")
            2 -> Log.e("talha", "Json Parsing Exception")
            0 -> {
                Log.e("api", "News data is taken for game %d".format(game.steamId))
                gameNewsNotifier.newsChanged(game)
                addNewsToDatabase(game)
            }
        }

    }

}


interface gameNewsNotifier {
    fun newsChanged(game: Game)
}


fun getNews(game: Game, gameNewsNotifier: gameNewsNotifier) {
    if (game.news.size == 0)
        GameNewsTaker(game, gameNewsNotifier).execute()
}