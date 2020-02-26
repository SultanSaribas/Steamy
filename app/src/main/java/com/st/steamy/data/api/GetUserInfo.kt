package com.st.steamy.data.api

import android.os.AsyncTask
import android.util.Log
import com.st.steamy.data.INITIALIZENAME
import com.st.steamy.data.INITIALIZEPHOTO
import com.st.steamy.data.INITIALIZEVISIBILITY
import com.st.steamy.data.image.getImage
import com.st.steamy.data.image.imageBackNotification
import com.st.steamy.data.permanent.addUserToDataBase
import com.st.steamy.data.storage.Game
import com.st.steamy.data.storage.MainUser
import com.st.steamy.data.storage.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


interface userBackNotification {
    fun userVisibilityChanged(user: User)
    fun userNameChanged(user: User)
    fun imageChanged(user: User)
}


fun getUserInfo(user: User, userBackNotification: userBackNotification) {
    if (user.visibility == INITIALIZEVISIBILITY)
        UserInfoTaker(user, userBackNotification).execute()
}





class ImageNotifierClass(val userBackNotification: userBackNotification) : imageBackNotification {
    override fun imageChanged(game: Game?, user: User?) {
        userBackNotification.imageChanged(user!!)
    }
}








class UserInfoTaker(val user: User, val userBackNotification: userBackNotification) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        var urlConnection: HttpURLConnection? = null

        var reader: BufferedReader? = null
        var ownedGamesJson: String? = null

        var quitEarly = false
        try {
            val url = URL(String.format("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=2FC18EF9153AB320CB64243B7343B033&steamids=%d", user.steamId))
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
            val infoObject = JSONObject(ownedGamesJson!!).getJSONObject("response").getJSONArray("players").getJSONObject(0)
            if (infoObject == null) {
                user.visibility = "unvisible"
            } else {
                user.name = infoObject.getString("personaname")

                try {
                    user.name = infoObject.getString("realname")
                } catch (ex: org.json.JSONException) {
                    Log.e("api", "real name is not shown for user %d".format(user.steamId))
                }

                user.photoLink = infoObject.getString("avatarmedium")
                getImage(null, user, ImageNotifierClass(userBackNotification))
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
                Log.e("api", "User info data is taken for user %d".format(user.steamId))
                userBackNotification.userNameChanged(user)
                userBackNotification.userVisibilityChanged(user)
                addUserToDataBase(user)
            }
        }

    }

}