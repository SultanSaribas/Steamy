package com.st.steamy.data.permanent

import android.content.ContentValues
import android.os.AsyncTask
import com.st.steamy.data.myContentResolver
import com.st.steamy.data.storage.MainUser

class AddMainUserInfoToDataBase(val list: Iterable<Long>, val isUser: Boolean) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        if (isUser) {
            val uriFriend = SteamyContentProvider.FOLLOWEDUSER_URI
            for (i in list) {
                val value = ContentValues()
                value.put(SteamyContract.FollowedGameEntry.COLUMN_STEAMID, i)
                myContentResolver?.insert(uriFriend, value)
            }
        } else {
            val uriGame = SteamyContentProvider.FOLLOWEDGAME_URI

            for (i in list) {
                val value = ContentValues()
                value.put(SteamyContract.FollowedUserEntry.COLUMN_STEAMID, i)
                myContentResolver?.insert(uriGame, value)
            }
        }


        return 0
    }
}


fun addFollowedUsersToDataBase(list: Iterable<Long>) {
    AddMainUserInfoToDataBase(list, true).execute()
}

fun addFollowedGamesToDataBase(list: Iterable<Long>) {
    AddMainUserInfoToDataBase(list, false).execute()
}
