package com.st.steamy.data.api

import android.os.AsyncTask
import android.util.Log
import com.st.steamy.FAGamesAdapter
import com.st.steamy.MPGamesAdapter
import com.st.steamy.MainActivity
import com.st.steamy.data.MAINUSER
import com.st.steamy.data.NOTIFY_ALL_ADAPTERS
import com.st.steamy.data.permanent.GetNotificationByDatabase
import com.st.steamy.data.permanent.addFollowedGamesToDataBase
import com.st.steamy.data.privateGame
import com.st.steamy.data.storage.MainUser
import com.st.steamy.data.storage.User
import com.st.steamy.data.storage.addGame
import com.st.steamy.data.storage.getGame
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class OwnedGamesTaker(val user: User, val faGamesAdapter: FAGamesAdapter) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        var urlConnection: HttpURLConnection? = null

        var reader: BufferedReader? = null
        var ownedGamesJson: String? = null

        var quitEarly = false
        try {
            val url = URL(String.format("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=2FC18EF9153AB320CB64243B7343B033&steamid=%d&format=json&include_appinfo=true", user.steamId))
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            val inputStream = urlConnection.inputStream

            if (inputStream != null) {
                reader = BufferedReader(InputStreamReader(inputStream))
                ownedGamesJson = reader.readLines().joinToString(separator = "\n")
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
            val ownedGamesObject = JSONObject(ownedGamesJson!!).getJSONObject("response")

            var count = ownedGamesObject.getInt("game_count")


            val ownedGamesArray = ownedGamesObject.getJSONArray("games")

            for (i in 0 until count) {
                val buff = ownedGamesArray.getJSONObject(i)
                val idVal = buff.getLong("appid")
                user.addGame(idVal)
                addGame(idVal, buff.getString("name"), buff.getString("img_icon_url")) // or img_logo_url
            }

        } catch (e: org.json.JSONException) {
            user.games.add(0L)
            Log.e("talha", "Error Message " + e.message)
            e.printStackTrace()
            Log.e("talha", ownedGamesJson ?:"Not Found string")
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
                Log.e("api", "Owned Games data is taken for user %d".format(user.steamId))
            }
        }
        faGamesAdapter.notifyDataSetChanged()
    }

}

class OwnedGamesTakerMainUser(val user: MainUser, val notify: GetNotificationByDatabase) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        var urlConnection: HttpURLConnection? = null

        var reader: BufferedReader? = null
        var ownedGamesJson: String? = null

        var quitEarly = false
        try {
            val url = URL(String.format("http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=2FC18EF9153AB320CB64243B7343B033&steamid=%d&format=json&include_appinfo=true", user.steamId))
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            val inputStream = urlConnection.inputStream

            if (inputStream != null) {
                reader = BufferedReader(InputStreamReader(inputStream))
                ownedGamesJson = reader.readLines().joinToString(separator = "\n")
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
            val ownedGamesObject = JSONObject(ownedGamesJson!!).getJSONObject("response")
            val count = ownedGamesObject.getInt("game_count")


            val ownedGamesArray = ownedGamesObject.getJSONArray("games")

            for (i in 0 until count) {
                val buff = ownedGamesArray.getJSONObject(i)
                val idVal = buff.getLong("appid")
                val title = buff.getString("name")
                val logoAddress = buff.getString("img_icon_url")// or img_logo_url
                user. followGame(idVal)
                addGame(idVal, title, logoAddress)
            }

        } catch (e: org.json.JSONException) {
            Log.e("talha", "Error Message " + e.message)
            e.printStackTrace()
            Log.e("talha", ownedGamesJson ?:"Not Found string")
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
                Log.e("api", "Owned Games data is taken for mainuser %d".format(user.steamId))
                addFollowedGamesToDataBase(user.followedGames)
                notify.completed("followedGame")
            }
        }
    }
}


fun getOwnedGames(user: User, faGamesAdapter: FAGamesAdapter) {
    if (user.games.size == 0)
        OwnedGamesTaker(user, faGamesAdapter).execute()
}

fun getOwnedAndFollowedGames(user: MainUser, notify: GetNotificationByDatabase) {
    if (user.followedGames.size == 0)
        OwnedGamesTakerMainUser(user, notify).execute()
}