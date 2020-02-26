package com.st.steamy.activity.abstraction

import android.os.Bundle
import com.st.steamy.data.storage.Game
import com.st.steamy.data.storage.News
import com.st.steamy.data.storage.getGame
import java.lang.Exception

var activeNews: News? = null


abstract class NewRelatedActivity: ActivityBase() {

    var news: News? = null

    fun getActiveNews(): News {
        return news as News
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(activeNews == null) {
            throw Exception("To call news related activity please initialize the active news")
        }
        news = activeNews
    }

}