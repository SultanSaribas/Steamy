package com.st.steamy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.st.steamy.activity.abstraction.ActivityWithAdapter
import com.st.steamy.activity.abstraction.MainUserRelatedActivity
import com.st.steamy.activity.abstraction.activeGame
import com.st.steamy.activity.abstraction.activeUser
import com.st.steamy.data.INITIALIZELOGO
import com.st.steamy.data.INITIALIZEPHOTO
import com.st.steamy.data.api.*
import com.st.steamy.data.image.getImage
import com.st.steamy.data.image.imageBackNotification
import com.st.steamy.data.storage.*
import kotlin.math.log

class MainProfileActivity : MainUserRelatedActivity(), ActivityWithAdapter {

    var mprvFragment : RecyclerFragment? = null
    var mgrvFragment : RecyclerFragment? = null
    val manager = supportFragmentManager
    var activeFragment = "mprv"
    val adapters : MutableList<RecyclerView.Adapter<RecyclerView.ViewHolder>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_profile)



        val mprvAdapter = MPFriendAdapter(getMainUser(), this)  as RecyclerView.Adapter<RecyclerView.ViewHolder>
        val mgrvAdapter = MPGamesAdapter(getMainUser(), this) as RecyclerView.Adapter<RecyclerView.ViewHolder>

        adapters.add(mprvAdapter)
        adapters.add(mgrvAdapter)

        addAdapters(adapters)

        mprvFragment = RecyclerFragment(mprvAdapter)
        mgrvFragment = RecyclerFragment(mgrvAdapter)

        getMainUser().willNotifyFriend = mprvAdapter
        getMainUser().willNotifyGame = mgrvAdapter



        val transaction = manager.beginTransaction()
        transaction.add(R.id.mainActivityContainer, mprvFragment!!, "mprv")
        transaction.add(R.id.mainActivityContainer, mgrvFragment!!, "mgrv")
        transaction.detach(mgrvFragment!!)
        transaction.commit()


        val maFriends = findViewById<Button>(R.id.maFriends)
        val maGames = findViewById<Button>(R.id.maGames)

        maFriends.setOnClickListener(View.OnClickListener { attach("mprv") })
        maGames.setOnClickListener(View.OnClickListener { attach("mgrv") })


    }

    override fun onDestroy() {
        super.onDestroy()
        delAdapters(adapters)
    }


    fun attach(name: String) {
        if (name == activeFragment)
            return
        if (name !in arrayOf("mprv","mgrv"))
            return

        val transaction = manager.beginTransaction()
        transaction.attach(manager.findFragmentByTag(name) as Fragment)
        transaction.detach(manager.findFragmentByTag(activeFragment) as Fragment)
        transaction.commit()
        activeFragment = name
    }


    fun goToFriend(user: User?) {
        val i = Intent(this@MainProfileActivity, FriendActivity::class.java)
        activeUser = user?.steamId
        startActivity(i)
    }

    fun goToGame(game: Game?) {
        val i = Intent(this@MainProfileActivity, GameNewsActivity::class.java)
        activeGame = game?.steamId
        startActivity(i)
    }


}

class MPFriendAdapter(val mainUser: MainUser, val mainProfileActivity: MainProfileActivity): RecyclerView.Adapter<MPFriendAdapter.MPFriendViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MPFriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mprv, parent, false) as View
        return MPFriendViewHolder(view, mainProfileActivity)
    }

    override fun getItemCount(): Int {
        return mainUser.friends.size
    }

    override fun onBindViewHolder(holder: MPFriendViewHolder, position: Int) {
        holder.bind(getUser(mainUser.friends[position]))
    }

    class MPFriendViewHolder: RecyclerView.ViewHolder, userBackNotification {
        var name: TextView
        var profilePhoto: ImageView
        var connectedUser: User? = null
        constructor(itemView: View, mainProfileActivity: MainProfileActivity) : super(itemView) {
            name = itemView.findViewById(R.id.mprvName)
            profilePhoto = itemView.findViewById(R.id.mprvProfilePhoto)
            itemView.findViewById<View>(R.id.mprvLayout).setOnClickListener(View.OnClickListener {
                mainProfileActivity.goToFriend(connectedUser)
            })
        }
        fun bind(user: User) {
            connectedUser = user
            name.setText(user.name)

            if (user.drawable==null) {
                profilePhoto.setImageResource(INITIALIZEPHOTO)
            } else {
                profilePhoto.setImageDrawable(user.drawable)
            }

            getUserInfo(user, this)
        }
        override fun userVisibilityChanged(user: User) {

        }
        override fun userNameChanged(user: User) {
            name.text = user.name
        }

        override fun imageChanged(user: User) {
            if (user == connectedUser) {
                profilePhoto.setImageDrawable(user.drawable)
            }
        }
    }
}

class MPGamesAdapter(val mainUser: MainUser, val mainProfileActivity: MainProfileActivity): RecyclerView.Adapter<MPGamesAdapter.MPGamesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MPGamesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mgrv, parent, false) as View
        return MPGamesViewHolder(view, mainProfileActivity)
    }

    override fun getItemCount(): Int {
        return mainUser.followedGames.size
    }

    override fun onBindViewHolder(holder: MPGamesViewHolder, position: Int) {
        holder.bind(getGame(mainUser.followedGames[position]))
    }

    class MPGamesViewHolder: RecyclerView.ViewHolder, gameNewsNotifier, imageBackNotification {
        var title: TextView
        var newNumber: TextView
        var logo: ImageView
        var connectedGame: Game? = null
        constructor(itemView: View, mainProfileActivity: MainProfileActivity) : super(itemView) {
            title = itemView.findViewById(R.id.mgrvTitle)
            newNumber = itemView.findViewById(R.id.mgrvNewsNumber)
            logo = itemView.findViewById(R.id.mgrvLogo)
            itemView.findViewById<View>(R.id.mgrvLinearLayout).setOnClickListener(View.OnClickListener {
                mainProfileActivity.goToGame(connectedGame)
            })
        }
        fun bind(game: Game) {
            connectedGame = game
            title.text = game.title
            newNumber.text = game.news.size.toString()

            Log.v("INFO", "http://media.steampowered.com/steamcommunity/public/images/apps/%d/%s.jpg".format(game.steamId, game.logoAdress))
            if(game.drawable==null) {
                logo.setImageResource(INITIALIZELOGO)
                getImage(game, null, this)
            } else {
                logo.setImageDrawable(game.drawable)
            }

            getNews(game, this)
        }
        override fun newsChanged(game: Game) {
            if (game == connectedGame)
                newNumber.text = game.news.size.toString()
        }

        override fun imageChanged(game: Game?, user: User?) {
            if (game == connectedGame)
                logo.setImageDrawable(game?.drawable)
        }
    }
}

