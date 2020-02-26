package com.st.steamy.serviceAndNotification

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import com.st.steamy.data.permanent.SteamyContentProvider
import com.st.steamy.data.permanent.SteamyContract
import com.st.steamy.data.storage.News
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class NewsUpdater : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        doBackgroundWork(params)
        return true
    }

    private fun doBackgroundWork(params: JobParameters?) {
        Thread(Runnable {
            val uriGame = SteamyContentProvider.FOLLOWEDGAME_URI
            val projectionGame = arrayOf(SteamyContract.FollowedGameEntry.COLUMN_STEAMID)
            val cursorGame = contentResolver?.query(uriGame, projectionGame, null, null, null)
            if (cursorGame != null) {
                var newNewsNumber = 0
                while (cursorGame.moveToNext()) {
                    val gameId = cursorGame.getLong(cursorGame.getColumnIndex(SteamyContract.FollowedGameEntry.COLUMN_STEAMID))
                    for (news in getNewsForUpdate(gameId)) {
                        val uri = SteamyContentProvider.NEWS_URI
                        val value = ContentValues()
                        value.put(SteamyContract.NewsEntry.COLUMN_NEWS_ID, "$gameId/${news.id}")
                        value.put(SteamyContract.NewsEntry.COLUMN_GAME_STEAMID, gameId)
                        value.put(SteamyContract.NewsEntry.COLUMN_TITLE, news.title)
                        value.put(SteamyContract.NewsEntry.COLUMN_CONTENT, news.content)
                        if (contentResolver.insert(uri, value) != null) {
                            newNewsNumber++
                        }
                    }
                }
                if (newNewsNumber>0) {
                    val intent = Intent("com.st.steamy.newNews")
                    intent.putExtra("newNumber",newNewsNumber)
                    sendBroadcast(intent)
                }
            }
        }).start()
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        jobFinished(params, true)
        return true
    }

}







fun getNewsForUpdate(gameId: Long): Iterable<News> {

    val res = mutableListOf<News>()

    var urlConnection: HttpURLConnection? = null

    var reader: BufferedReader? = null
    var newsListJson: String? = null

    var quitEarly = false
    try {
        val url = URL(String.format("http://api.steampowered.com/ISteamNews/GetNewsForApp/v0002/?appid=%d&count=15&maxlength=8000&format=json", gameId))
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

    if (quitEarly)
        return res

    try {
        val newsListArray = JSONObject(newsListJson!!).getJSONObject("appnews").getJSONArray("newsitems")


        for (i in 0 until newsListArray.length()) {
            val news = newsListArray.getJSONObject(i)
            val newsObject = News(news.getLong("gid"))
            res.add(newsObject)
        }

    } catch (e: org.json.JSONException) {
        Log.e("talha", "Error Message " + e.message)
        e.printStackTrace()
        Log.e("talha", newsListJson ?:"Not Found string")
        Log.v("talha", "Json Fail Printing Console")
        quitEarly = true
    }

    return res

}