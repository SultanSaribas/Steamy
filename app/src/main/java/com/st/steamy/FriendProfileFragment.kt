package com.st.steamy


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.st.steamy.activity.abstraction.ActivityWithAdapter
import com.st.steamy.data.MAINUSER
import com.st.steamy.data.NOTIFY_ALL_ADAPTERS
import com.st.steamy.data.storage.User

class FriendProfileFragment(val user: User) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.e("USERINFO","%s is drawable \n%s photo link\n%d id\n%s name".format(
            if(user.drawable==null) "NO" else "Yes",
            user.photoLink ?: "" ,
            user.steamId,
            user.name))


        val view = inflater.inflate(R.layout.fragment_friend_profile, container, false)

        val fpFollow = view.findViewById<Button>(R.id.fpFollow)
        val fpPhoto = view.findViewById<ImageView>(R.id.fpPhoto)
        val fpName = view.findViewById<TextView>(R.id.fpName)
        val fpId = view.findViewById<TextView>(R.id.fpId)
        val fpVisibility = view.findViewById<TextView>(R.id.fpVisibility)

        fpFollow.setText(if (user.steamId in MAINUSER!!.friends ) resources.getText(R.string.unfollow) else resources.getText(R.string.follow))
        fpFollow.setOnClickListener(View.OnClickListener {
            if (user.steamId in MAINUSER!!.friends){
                MAINUSER!!.friends -= user.steamId
                fpFollow.setText(resources.getText(R.string.follow))
                NOTIFY_ALL_ADAPTERS()
            } else {
                MAINUSER!!.friends += user.steamId
                fpFollow.setText(resources.getString(R.string.unfollow))
                NOTIFY_ALL_ADAPTERS()
            }
        })
        fpPhoto.setImageDrawable(user.drawable)
        fpName.setText(user.name)
        fpId.setText(user.steamId.toString())
        fpVisibility.setText(user.visibility)
        return view

    }


}

/*
inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recycler, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFragment)
        recyclerView.setAdapterByNameAndUser(type, userTracker.peek())
        adapter = recyclerView.adapter
        return view
 */