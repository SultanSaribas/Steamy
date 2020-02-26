package com.st.steamy.serviceAndNotification

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context.JOB_SCHEDULER_SERVICE
import com.st.steamy.MainActivity

fun setJobScheduler(act: MainActivity) {
    val componentName = ComponentName(act, NewsUpdater::class.java)
    val info = JobInfo.Builder(123, componentName)
        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) // wifi
        .setPeriodic(6 * 60 * 60 * 1000) // 6 hour
        .build()

    val scheduler = act.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
    scheduler.schedule(info)
}