package com.st.steamy.data.storage

import com.st.steamy.data.INITIALIZENAME
import com.st.steamy.data.INITIALIZEVISIBILITY
import com.st.steamy.data.privateUser

val userCollection: MutableMap<Long, User> = mutableMapOf()



fun getUser(id: Long): User {
    if (id==0L) {
        return privateUser!!
    }
    val res = userCollection[id]
    if (res == null) {
        val user = User(id, INITIALIZENAME, INITIALIZEVISIBILITY)
        userCollection[id] = user
        return user
    }
    return res
}
