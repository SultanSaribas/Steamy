package com.st.steamy.data

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.st.steamy.R
import com.st.steamy.data.storage.Game
import com.st.steamy.data.storage.MainUser
import com.st.steamy.data.storage.User

val INITIALIZEPHOTO: Int = R.drawable.wait
val INITIALIZELOGO: Int = R.drawable.wait
val INITIALIZENAME: String = "あなたの忍耐に感謝します"
val INITIALIZEVISIBILITY: String = "..."
var MAINUSER: MainUser? = null


val ADAPTERS: MutableList<RecyclerView.Adapter<RecyclerView.ViewHolder>> = mutableListOf()

fun NOTIFY_ALL_ADAPTERS() {
    for (i in ADAPTERS) {
        i.notifyDataSetChanged()
    }
}

var myResources: Resources? = null
var privateUser: User? = null
var privateGame: Game? = null

var notReachDrawable: Drawable? = null

var myContentResolver: ContentResolver? = null

var myContext: Context? = null