package com.st.steamy.data.storage

import android.graphics.drawable.Drawable


class Game(val steamId: Long, var title: String) {
    val news: MutableSet<News> = mutableSetOf()
    var logoAdress: String? = null
    var drawable: Drawable? = null

    fun addNews(news: News) {
        this.news.add(news)
    }

}