package com.st.steamy.serviceAndNotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.format.Time
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NewNewsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        var builder = NotificationCompat.Builder(context)
            .setContentTitle("Game News")
            .setContentText("Steamy collected ${intent.getIntExtra("newNumber",-1)} new news from your games")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(Time().toMillis(true).toInt(), builder.build())
        }


    }
}
