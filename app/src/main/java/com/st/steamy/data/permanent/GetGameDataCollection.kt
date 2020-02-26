package com.st.steamy.data.permanent

import android.os.AsyncTask
import android.util.Log
import com.st.steamy.data.myContentResolver
import com.st.steamy.data.storage.Game
import com.st.steamy.data.storage.gameCollection

class GetGameDataCollection(val notify: GetNotificationByDatabase) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        val uri = SteamyContentProvider.GAME_URI
        val projection = arrayOf(SteamyContract.GameEntry.COLUMN_NAME,
            SteamyContract.GameEntry.COLUMN_STEAMID,
            SteamyContract.GameEntry.COLUMN_LOGO)


        val cursor = myContentResolver?.query(uri, projection, null, null, null)

        if (cursor!=null) {
            while (cursor.moveToNext()) {
                val newGame = Game(cursor.getLong(cursor.getColumnIndex(SteamyContract.GameEntry.COLUMN_STEAMID)),
                    cursor.getString(cursor.getColumnIndex(SteamyContract.GameEntry.COLUMN_NAME)))
                newGame.logoAdress = cursor.getString(cursor.getColumnIndex(SteamyContract.GameEntry.COLUMN_LOGO))
                gameCollection[newGame.steamId] = newGame
            }
        }

        notify.completed("games")
        cursor?.close()
        return 0
    }
}

fun getGameDataCollection(notify: GetNotificationByDatabase) {
    GetGameDataCollection(notify).execute()
}

