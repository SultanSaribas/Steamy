package com.st.steamy.activity.abstraction

import android.os.Bundle
import com.st.steamy.data.MAINUSER
import com.st.steamy.data.storage.MainUser

var mainUser: MainUser? = null

abstract class MainUserRelatedActivity: ActivityBase() {

    fun getMainUser(): MainUser {
        return mainUser as MainUser
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(mainUser == null) {
            throw Exception("To call main user related activity please initialize the active user")
        }
        MAINUSER = mainUser

    }
}