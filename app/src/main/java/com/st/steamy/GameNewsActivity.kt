package com.st.steamy

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.st.steamy.activity.abstraction.GameRelatedActivity
import com.st.steamy.activity.abstraction.activeNews
import com.st.steamy.data.MAINUSER
import com.st.steamy.data.NOTIFY_ALL_ADAPTERS
import com.st.steamy.data.storage.getUser

class GameNewsActivity : GameRelatedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_news)

        val gnLogo = findViewById<ImageView>(R.id.gnLogo)
        val gnFollow = findViewById<Button>(R.id.gnFollow)
        val gnGameName = findViewById<TextView>(R.id.gnGameName)
        val gnFriendList = findViewById<TextView>(R.id.gnFriendList)
        val gnLinearLayout = findViewById<LinearLayout>(R.id.gnLinearLayout)

        val game = getActiveGame()


        val bitmap = (game.drawable as BitmapDrawable).bitmap
        val newDrawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 350, 350, true))


        gnLogo.setImageDrawable(newDrawable)
        gnFollow.text = if (game.steamId in MAINUSER!!.followedGames) resources.getText(R.string.unfollow) else resources.getText(R.string.follow)
        gnFollow.setOnClickListener(View.OnClickListener {
            if (game.steamId in MAINUSER!!.followedGames) {
                MAINUSER!!.followedGames -= game.steamId
                gnFollow.text = resources.getText(R.string.follow)
                NOTIFY_ALL_ADAPTERS()
            } else {
                MAINUSER!!.followedGames += game.steamId
                gnFollow.text = resources.getText(R.string.unfollow)
                NOTIFY_ALL_ADAPTERS()
            }
        })

        gnGameName.text = game.title
        gnFriendList.text = MAINUSER!!.friends.map { getUser(it) }
            .filter { game.steamId in it.games }
            .map { it.name }
            .joinToString(separator="\n",prefix = resources.getText(R.string.peopleWhoHaveThisGame))

        for (i in game.news) {
            val title = Button(this)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
                .apply {
                    leftMargin = 10
                    rightMargin = 10
                    topMargin = 10
                }

            gnLinearLayout.addView(title)

            title.layoutParams = layoutParams

            title.gravity = Gravity.CENTER
            title.text = i.title
            title.background = resources.getDrawable(R.drawable.news_background)
            title.setTextColor(resources.getColor(R.color.colorPrimary))
            //title.setBackgroundColor(resources.getColor(R.color.background))
            title.setTypeface(null, Typeface.BOLD)
            title.setOnClickListener(View.OnClickListener {
                val intent = Intent(this@GameNewsActivity, NewsInfoActivity::class.java)
                activeNews = i
                startActivity(intent)
            })

        }


    }
}
