package com.st.steamy.activity.abstraction

import android.os.Bundle
import com.st.steamy.data.storage.User
import com.st.steamy.data.storage.getUser
import java.lang.Exception

var activeUser: Long? = null

abstract class UserRelatedActivity: ActivityBase() {

    var user: User? = null

    fun getActiveUser(): User {
        return user as User
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(activeUser == null) {
            throw Exception("To call user related activity please initialize the active user")
        }
        user = getUser(activeUser as Long)
    }

}