package com.st.steamy.data.permanent

import android.content.ContentValues
import android.os.AsyncTask
import com.st.steamy.data.myContentResolver
import com.st.steamy.data.storage.User

class AddUserToDataBase(val user: User) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        val uri = SteamyContentProvider.USER_URI
        val value = ContentValues()
        value.put(SteamyContract.UserEntry.COLUMN_NAME, user.name)
        value.put(SteamyContract.UserEntry.COLUMN_STEAMID, user.steamId)
        value.put(SteamyContract.UserEntry.COLUMN_PHOTO, user.photoLink)
        myContentResolver?.insert(uri, value)
        return 0
    }
}

fun addUserToDataBase(user: User) {
    AddUserToDataBase(user).execute()
}