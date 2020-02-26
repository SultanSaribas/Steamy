package com.st.steamy

import android.os.Bundle
import android.text.Html
import android.widget.TextView
import com.st.steamy.activity.abstraction.NewRelatedActivity

class NewsInfoActivity : NewRelatedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_info)

        val news = getActiveNews()

        val niTitle = findViewById<TextView>(R.id.niTitle)
        val niContent = findViewById<TextView>(R.id.niContext)

        niTitle.text = news.title

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            niContent.setText(Html.fromHtml(news.content,Html.FROM_HTML_MODE_LEGACY));
        } else {
            niContent.setText(Html.fromHtml(news.content));
        }

    }
}
