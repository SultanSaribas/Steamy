package com.st.steamy.data.image

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.Log
import com.st.steamy.R
import com.st.steamy.data.INITIALIZENAME
import com.st.steamy.data.notReachDrawable
import com.st.steamy.data.permanent.AddPictureToDevice
import com.st.steamy.data.permanent.addPictureToDevice
import com.st.steamy.data.permanent.getPictureFromDevice
import com.st.steamy.data.storage.Game
import com.st.steamy.data.storage.User
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


interface imageBackNotification {
    fun imageChanged(game: Game?, user: User?)
}

fun getImage(game: Game?, user: User?, imageBackNotification: imageBackNotification) {
    if (game!=null) {
        val data = getPictureFromDevice("game_${game.steamId}")
        if (data != null) {
            game.drawable = data
            NotSure(game, user, imageBackNotification).execute()
        } else {
            val res = "http://media.steampowered.com/steamcommunity/public/images/apps/%d/%s.jpg".format(game.steamId, game.logoAdress)
            ImageTaker(game, user, imageBackNotification, res).execute()
        }

    }
    if (user!=null) {
        val data = getPictureFromDevice("user_${user.steamId}")
        if (data != null) {
            user.drawable = data
            NotSure(game, user, imageBackNotification).execute()
        } else {
            val res = user.photoLink!!
            ImageTaker(game, user, imageBackNotification, res).execute()
        }

    }
}

class NotSure(val game: Game?, val user: User?, val imageBackNotification: imageBackNotification) : AsyncTask<Any, Any, Any>() {
    override fun doInBackground(vararg params: Any?): Any {
        return 0
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        imageBackNotification.imageChanged(game, user)
    }
}



class ImageTaker(val game: Game?, val user: User?, val imageBackNotification: imageBackNotification, val resource: String) : AsyncTask<Any, Any, Int>() {
    override fun doInBackground(vararg params: Any?): Int {
        var res: Bitmap? = null
        var conn: HttpURLConnection? = null
        var quitEarly = false
        if (game==null && user==null) {
            return 3
        }
        if (game!=null && user!=null) {
            return 4
        }

        try {
            conn = URL(resource).openConnection() as HttpURLConnection
            conn.connect()
            res = BitmapFactory.decodeStream(conn.inputStream)
        } catch (e: IOException) {
            Log.e("talha", "Error Message " + e.message)
            e.printStackTrace()
            quitEarly = true
        } finally {
            conn?.disconnect()
        }
        if (quitEarly) return 1

        if(game!=null) {
            game.drawable = BitmapDrawable(Resources.getSystem(), res)
            addPictureToDevice("game_${game.steamId}", game.drawable!!)
        } else {
            user!!.drawable = BitmapDrawable(Resources.getSystem(), res)
            addPictureToDevice("user_${user.steamId}", user.drawable!!)
        }

        return 0
    }

    override fun onPostExecute(bla: Int?) {
        super.onPostExecute(bla)

        if(bla != 0) {
            if (user != null)
                user.drawable = notReachDrawable
            if (game != null)
                game.drawable = notReachDrawable
        }

        when (bla) {
            1 -> Log.e("talha", "Internet Exception")
            3 -> Log.e("talha", "Both game and user are null")
            4 -> Log.e("talha", "Both game and user not null")
            0 -> {
                Log.e("api", "Image is taken for id %d %s".format(if(game!=null) game.steamId else user!!.steamId, imageBackNotification))
                imageBackNotification.imageChanged(game, user)
            }
        }

    }




}