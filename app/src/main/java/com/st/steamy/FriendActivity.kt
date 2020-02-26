package com.st.steamy

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
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
import com.squareup.picasso.Picasso
import com.st.steamy.activity.abstraction.ActivityWithAdapter
import com.st.steamy.activity.abstraction.UserRelatedActivity
import com.st.steamy.activity.abstraction.activeGame
import com.st.steamy.activity.abstraction.activeUser
import com.st.steamy.data.INITIALIZELOGO
import com.st.steamy.data.INITIALIZEPHOTO
import com.st.steamy.data.api.*
import com.st.steamy.data.image.getImage
import com.st.steamy.data.image.imageBackNotification
import com.st.steamy.data.storage.Game
import com.st.steamy.data.storage.User
import com.st.steamy.data.storage.getGame
import com.st.steamy.data.storage.getUser

class FriendActivity : UserRelatedActivity(), ActivityWithAdapter {

    var friendFragment : FriendProfileFragment? = null
    var mgrvFragment : RecyclerFragment? = null
    var mprvFragment : RecyclerFragment? = null
    val manager = supportFragmentManager
    var activeFragment = "profile"
    val adapters : MutableList<RecyclerView.Adapter<RecyclerView.ViewHolder>> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

        val mgrvAdapter = FAGamesAdapter(getActiveUser(), this) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        val mprvAdapter = FAFriendAdapter(getActiveUser(), this) as RecyclerView.Adapter<RecyclerView.ViewHolder>

        adapters.add(mgrvAdapter)
        adapters.add(mprvAdapter)

        addAdapters(adapters)

        friendFragment = FriendProfileFragment(getActiveUser())
        mgrvFragment = RecyclerFragment(mgrvAdapter)
        mprvFragment = RecyclerFragment(mprvAdapter)



        val transaction = manager.beginTransaction()
        transaction.add(R.id.friendActivityContainer, friendFragment!!, "profile")
        transaction.add(R.id.friendActivityContainer, mgrvFragment!!, "mgrv")
        transaction.add(R.id.friendActivityContainer, mprvFragment!!, "mprv")
        transaction.detach(mgrvFragment!!)
        transaction.detach(mprvFragment!!)
        transaction.commit()


        val faProfile = findViewById<Button>(R.id.faProfile)
        val faGames = findViewById<Button>(R.id.faGames)
        val faFriends = findViewById<Button>(R.id.faFriends)


        faProfile.setOnClickListener(View.OnClickListener { attach("profile") })
        faGames.setOnClickListener(View.OnClickListener { attach("mgrv") })
        faFriends.setOnClickListener(View.OnClickListener { attach("mprv") })


    }

    override fun onDestroy() {
        super.onDestroy()
        delAdapters(adapters)
    }

    fun goToFriend(user: User?) {
        if(user == null) {
            Log.e("Talha","user is empty")
            return
        }

        val i = Intent(this@FriendActivity, FriendActivity::class.java)
        activeUser = user.steamId
        startActivity(i)
    }

    fun goToGame(game: Game?) {
        if(game == null) {
            Log.e("Talha", "game is empty")
            return
        }

        val i = Intent(this@FriendActivity, GameNewsActivity::class.java)
        activeGame = game.steamId
        startActivity(i)
    }

    fun attach(name: String) {
        Log.v("talha","hello")
        if (name == activeFragment)
            return
        if (name !in arrayOf("profile","mgrv","mprv"))
            return

        val transaction = manager.beginTransaction()
        transaction.attach(manager.findFragmentByTag(name) as Fragment)
        transaction.detach(manager.findFragmentByTag(activeFragment) as Fragment)
        transaction.commit()
        activeFragment = name
    }

}

class FAGamesAdapter(val user: User, val friendActivity: FriendActivity): RecyclerView.Adapter<FAGamesAdapter.FAGamesViewHolder>() {

    init {
        getOwnedGames(user, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAGamesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mgrv, parent, false) as View
        return FAGamesViewHolder(view, friendActivity, parent.context)
    }

    override fun getItemCount(): Int {
        return user.games.size
    }

    override fun onBindViewHolder(holder: FAGamesViewHolder, position: Int) {
        holder.bind(getGame(user.games[position]))
    }

    class FAGamesViewHolder: RecyclerView.ViewHolder, gameNewsNotifier, imageBackNotification {

        var title: TextView
        var newNumber: TextView
        var logo: ImageView
        var connectedGame: Game? = null
        val context: Context
        constructor(itemView: View, friendActivity: FriendActivity, context: Context) : super(itemView) {
            title = itemView.findViewById(R.id.mgrvTitle)
            newNumber = itemView.findViewById(R.id.mgrvNewsNumber)
            logo = itemView.findViewById(R.id.mgrvLogo)
            itemView.findViewById<View>(R.id.mgrvLinearLayout).setOnClickListener(View.OnClickListener {
                friendActivity.goToGame(connectedGame)
            })
            this.context = context
        }
        fun bind(game: Game) {
            connectedGame = game
            title.setText(game.title)
            newNumber.setText(game.news.size.toString())

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
            if (game == game)
                newNumber.text = game.news.size.toString()
        }

        override fun imageChanged(game: Game?, user: User?) {
            if (game == connectedGame) {
                logo.setImageDrawable(game!!.drawable)
            }
        }
    }
}

class FAFriendAdapter(val user: User, val friendActivity: FriendActivity): RecyclerView.Adapter<FAFriendAdapter.FAFriendViewHolder>() {

    init {
        getFriendList(user, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAFriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mprv, parent, false) as View
        return FAFriendViewHolder(view, friendActivity)
    }

    override fun getItemCount(): Int {
        return user.friends.size
    }

    override fun onBindViewHolder(holder: FAFriendViewHolder, position: Int) {
        holder.bind(getUser(user.friends[position]))
    }

    class FAFriendViewHolder: RecyclerView.ViewHolder, userBackNotification {
        var name: TextView
        var profilePhoto: ImageView
        var connectedUser: User? = null
        constructor(itemView: View, friendActivity: FriendActivity) : super(itemView) {
            name = itemView.findViewById(R.id.mprvName)
            profilePhoto = itemView.findViewById(R.id.mprvProfilePhoto)
            itemView.findViewById<View>(R.id.mprvLayout).setOnClickListener(View.OnClickListener {
                friendActivity.goToFriend(connectedUser)
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
            if (user == connectedUser)
                name.text = user.name
        }

        override fun imageChanged(user: User) {
            if (user == connectedUser) {
                profilePhoto.setImageDrawable(user.drawable)
            }
        }
    }
}
