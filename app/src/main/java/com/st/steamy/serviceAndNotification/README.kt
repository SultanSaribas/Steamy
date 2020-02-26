package com.st.steamy.serviceAndNotification

/*

    - There are two classes and a function added to handle this part
    - In database "ON CONFLICT REPLACE" changed to "ON CONFLICT IGNORE" for news table
        so that when already existed new tried to added to data base it will return null


        *) NewsUpdater
            -It is a job service
            -It reads followed games from db and
                get news for that id
                then try to add data base
                then if it is not in db already it increases newNewsNumber
                after all check, if new news number is more than 0 it sends "com.st.steamy.newNews" to broadcast receivers

        *) NewNewsBroadcastReceiver
            -It is a broadcast receiver
            -It looks for intent whose action is "com.st.steamy.newNews"
            -when action come
                it create notification that has
                    "Game News" as title
                    "Steamy collected $newNewsNumber new news from your games" as content
                then shows to user

        *) setJobScheduler
            -It is function to put news updater to scheduler
            -It does it once a 6 hours while wifi is open
                wifi is necessary to get data internet
                once a six hours is sufficient because
                    it will use less internet
                    news doesn't come so frequent and
                    it is not too important to learn a game new as soon as it exist








 */