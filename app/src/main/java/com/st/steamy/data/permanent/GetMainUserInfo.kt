package com.st.steamy.data.permanent

import android.os.AsyncTask
import com.st.steamy.activity.abstraction.mainUser
import com.st.steamy.data.myContentResolver

class GetMainUserInfo(val notify: GetNotificationByDatabase) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        val uriGame = SteamyContentProvider.FOLLOWEDGAME_URI
        val projectionGame = arrayOf(SteamyContract.FollowedGameEntry.COLUMN_STEAMID)
        val cursorGame = myContentResolver?.query(uriGame, projectionGame, null, null, null)

        val uriUser = SteamyContentProvider.FOLLOWEDUSER_URI
        val projectionUser = arrayOf(SteamyContract.FollowedUserEntry.COLUMN_STEAMID)
        val cursorUser = myContentResolver?.query(uriUser, projectionUser, null, null, null)

        if (cursorGame != null) {
            while (cursorGame.moveToNext()) {
                mainUser?.followGame(cursorGame.getLong(cursorGame.getColumnIndex(SteamyContract.FollowedGameEntry.COLUMN_STEAMID)))
            }
        }

        if (cursorUser != null) {
            while (cursorUser.moveToNext()) {
                mainUser?.follow(cursorUser.getLong(cursorUser.getColumnIndex(SteamyContract.FollowedUserEntry.COLUMN_STEAMID)))
            }
        }

        notify.completed("followedGame")
        notify.completed("followedUser")
        cursorGame?.close()
        cursorUser?.close()
        return 0
    }
}

fun getMainUserInfo(notify: GetNotificationByDatabase) {
    GetMainUserInfo(notify).execute()
}
