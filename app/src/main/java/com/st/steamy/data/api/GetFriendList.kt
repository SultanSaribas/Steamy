package com.st.steamy.data.api

import android.os.AsyncTask
import android.util.Log
import com.st.steamy.FAFriendAdapter
import com.st.steamy.MainActivity
import com.st.steamy.data.permanent.GetNotificationByDatabase
import com.st.steamy.data.permanent.addFollowedUsersToDataBase
import com.st.steamy.data.storage.MainUser
import com.st.steamy.data.storage.User
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class FriendListTaker(val user: User, val faFriendAdapter: FAFriendAdapter) : AsyncTask<Any,Any,Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        var urlConnection: HttpURLConnection? = null

        var reader: BufferedReader? = null
        var friendListJson: String? = null

        var quitEarly = false
        try {
            val url = URL(String.format("http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=2FC18EF9153AB320CB64243B7343B033&steamid=%d&relationship=friend", user.steamId))
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            val inputStream = urlConnection.inputStream

            if (inputStream != null) {
                reader = BufferedReader(InputStreamReader(inputStream))
                friendListJson = reader.readLines().joinToString(separator = "\n")
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
            val friendListArray = JSONObject(friendListJson!!).getJSONObject("friendslist").getJSONArray("friends")

            val length = friendListArray.length()
            if(length==0) {
                user.addFriend(0L)
            } else {
                for (i in 0 until length) {
                    user.addFriend(friendListArray.getJSONObject(i).getString("steamid").toLong())
                }
            }

            //user.addFriend(friendListArray.getJSONObject(i).getLong("steamid"))

        } catch (e: org.json.JSONException) {
            Log.e("talha", "Error Message " + e.message)
            e.printStackTrace()
            Log.e("talha", friendListJson ?:"Not Found string")
            Log.e("talha", "Json Fail Printing Console")
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
                Log.e("api", "Friend data is taken for user %d".format(user.steamId))

            }
        }
        faFriendAdapter.notifyDataSetChanged()

    }

}


class FriendListTakerMainUser(val user: MainUser, val notify: GetNotificationByDatabase) : AsyncTask<Any,Any,Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        var urlConnection: HttpURLConnection? = null

        var reader: BufferedReader? = null
        var friendListJson: String? = null

        var quitEarly = false
        try {
            val url = URL(String.format("http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=2FC18EF9153AB320CB64243B7343B033&steamid=%d&relationship=friend", user.steamId))
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            val inputStream = urlConnection.inputStream

            if (inputStream != null) {
                reader = BufferedReader(InputStreamReader(inputStream))
                friendListJson = reader.readLines().joinToString(separator = "\n")
            }
        } catch (e: IOException) {
            user.follow(0L)
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
            val friendListArray = JSONObject(friendListJson!!).getJSONObject("friendslist").getJSONArray("friends")

            val length = friendListArray.length()

            if (length == 0) {
                user.follow(0L)
            } else {
                for (i in 0 until length) {
                    user.follow(friendListArray.getJSONObject(i).getString("steamid").toLong())
                }
            }


        } catch (e: org.json.JSONException) {
            Log.e("talha", "Error Message " + e.message)
            e.printStackTrace()
            Log.e("talha", friendListJson ?:"Not Found string")
            Log.v("talha", "Json Fail Printing Console")
            quitEarly = true
        }


        Log.e("talha","user friend list %s".format(user.friends.joinToString()))
        Log.e("talha", "answer is %s".format(friendListJson))

        return if (quitEarly) 2 else 0
    }

    override fun onPostExecute(bla: Int?) {
        super.onPostExecute(bla)
        when (bla) {
            1 -> Log.e("talha", "Internet Exception")
            2 -> Log.e("talha", "Json Parsing Exception")
            0 -> {
                Log.e("api", "Friend data is taken for user %d".format(user.steamId))
                addFollowedUsersToDataBase(user.friends)
                notify.completed("followedUser")
            }
        }
        user.willNotifyFriend?.notifyDataSetChanged()

    }

}

fun getFriendList(user: User, faFriendAdapter: FAFriendAdapter) {
    if (user.friends.size==0)
        FriendListTaker(user, faFriendAdapter).execute()
}

fun getFriendList(user: MainUser, notify: GetNotificationByDatabase) {
    FriendListTakerMainUser(user, notify).execute()
}
