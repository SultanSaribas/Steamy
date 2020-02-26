package com.st.steamy.data.permanent

import android.os.AsyncTask
import com.st.steamy.data.myContentResolver
import com.st.steamy.data.storage.User
import com.st.steamy.data.storage.userCollection

class GetUserDataCollection(val notify: GetNotificationByDatabase) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        val uri = SteamyContentProvider.USER_URI
        val projection = arrayOf(SteamyContract.UserEntry.COLUMN_NAME,
            SteamyContract.UserEntry.COLUMN_STEAMID,
            SteamyContract.UserEntry.COLUMN_PHOTO)


        val cursor = myContentResolver?.query(uri, projection, null, null, null)

        if (cursor!=null) {
            while (cursor.moveToNext()) {
                val newUser = User(cursor.getLong(cursor.getColumnIndex(SteamyContract.UserEntry.COLUMN_STEAMID)),
                    cursor.getString(cursor.getColumnIndex(SteamyContract.UserEntry.COLUMN_NAME)),
                    "..."
                )
                newUser.photoLink = cursor.getString(cursor.getColumnIndex(SteamyContract.UserEntry.COLUMN_PHOTO))
                userCollection[newUser.steamId] = newUser
            }
        }
        notify.completed("users")
        cursor?.close()
        return 0
    }
}

fun getUserDataCollection(notify: GetNotificationByDatabase) {
    GetUserDataCollection(notify).execute()
}



