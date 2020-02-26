package com.st.steamy.data.storage

data class News(val id: Long) {
    var title: String? = null
    var content: String? = null
    fun setData(title: String, content: String) {
        this.title = title
        this.content = content
    }
}