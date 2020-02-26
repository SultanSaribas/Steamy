package com.st.steamy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.text.isDigitsOnly
import com.st.steamy.activity.abstraction.mainUser
import com.st.steamy.data.api.getFriendList
import com.st.steamy.data.api.getOwnedAndFollowedGames
import com.st.steamy.data.permanent.*
import com.st.steamy.data.storage.Game
import com.st.steamy.data.storage.MainUser
import com.st.steamy.data.storage.User
import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences.Editor
import android.preference.PreferenceManager
import android.content.SharedPreferences
import android.util.Log
import com.st.steamy.data.*
import com.st.steamy.serviceAndNotification.setJobScheduler


class MainActivity : AppCompatActivity(), GetNotificationByDatabase {

    var userIDEditText: EditText? = null
    var userIDAcceptButton: Button? = null
    var userIDGetText: TextView? = null
    var newIdButton: Button? = null
    var oldIdButton: Button? = null
    var waitingImageView: ImageView? = null
    var stepName: String = "null"
    var ifNewUser = false

    val continueCheck: MutableMap<String, Boolean> = mutableMapOf("followedUser" to false,
        "followedGame" to false,
        "games" to false,
        "users" to false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myResources = resources
        myContentResolver = contentResolver
        myContext = this

        privateUser = User(0, resources.getString(R.string.privateUserName), "...")
        privateUser?.drawable = resources.getDrawable(R.drawable.privateuser)
        privateGame = Game(0, resources.getString(R.string.privateGameName))
        privateGame?.drawable = resources.getDrawable(R.drawable.privategame)
        notReachDrawable = resources.getDrawable(R.drawable.notreach)

        userIDEditText = findViewById(R.id.isUserId)
        userIDAcceptButton = findViewById(R.id.isUserIdAccept)
        userIDGetText = findViewById(R.id.idGetText)
        newIdButton = findViewById(R.id.newId)
        oldIdButton = findViewById(R.id.oldId)
        waitingImageView = findViewById(R.id.waitingImageView)

        setJobScheduler(this)

        step1()
    }

    override fun completed(task: String) {
        continueCheck[task] = true
        Log.e("completeTask","$task is completed")

        if (continueCheck["games"]!!) {
            getNewsFromDataBase()
        }

        if (continueCheck["followedUser"]!! &&
                continueCheck["followedGame"]!! &&
                continueCheck["games"]!! &&
                continueCheck["users"]!!) {
            goToMainProfile()
        }

    }

    fun step1() {
        stepName = "step1"
        ifNewUser = false
        newIdButton!!.visibility = Button.VISIBLE
        oldIdButton!!.visibility = Button.VISIBLE
        userIDAcceptButton?.visibility  = Button.INVISIBLE
        userIDEditText?.visibility      = EditText.INVISIBLE
        userIDGetText?.visibility       = TextView.INVISIBLE
        waitingImageView?.visibility    = ImageView.INVISIBLE
        getUserDataCollection(this)
        getGameDataCollection(this)
        newIdButton!!.setOnClickListener(View.OnClickListener {
            if (stepName == "step1") {
                ifNewUser = true
                stepEnterId()
            }
        })


        val prefs = this.getSharedPreferences("ID", Context.MODE_PRIVATE)
            PreferenceManager.getDefaultSharedPreferences(this)


        if (prefs.contains("ID")) {
            oldIdButton!!.setOnClickListener(View.OnClickListener {
                if (stepName == "step1") {
                    stepGetMainUserInfo(prefs.getString("ID", "0")?:"0")
                }
            })
        } else {
            oldIdButton!!.setOnClickListener({
                if (stepName == "step1") {
                    Toast.makeText(applicationContext, resources.getText(R.string.noSavedUser), Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    fun stepEnterId() {
        stepName = "stepEnterId"
        newIdButton!!.visibility         = Button.INVISIBLE
        oldIdButton!!.visibility         = Button.INVISIBLE
        userIDAcceptButton!!.visibility  = Button.VISIBLE
        userIDEditText!!.visibility      = EditText.VISIBLE
        userIDGetText!!.visibility       = TextView.VISIBLE


        userIDAcceptButton!!.setOnClickListener(View.OnClickListener {
            if(checkUserId(userIDEditText!!.text.toString())) {
                // TODO delete old followed
                stepGetMainUserInfo(userIDEditText!!.text.toString())
            }
        })


    }

    fun stepGetMainUserInfo(id: String) {
        stepName = "stepGetMainUserInfo"
        mainUser = MainUser(id.toLong())
        if (ifNewUser) {
            getOwnedAndFollowedGames(mainUser!!, this)
            getFriendList(mainUser!!, this)
        } else {
            getMainUserInfo(this)
        }

        val prefs = this.getSharedPreferences("ID", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("ID", id)
        editor.commit()

        stepWait()
    }

    fun stepWait() {
        stepName = "stepWait"
        newIdButton?.visibility         = Button.INVISIBLE
        oldIdButton?.visibility         = Button.INVISIBLE
        userIDAcceptButton?.visibility  = Button.INVISIBLE
        userIDEditText?.visibility      = EditText.INVISIBLE
        userIDGetText?.visibility       = TextView.INVISIBLE
        waitingImageView?.visibility    = ImageView.VISIBLE
    }

    fun checkUserId(userId: String): Boolean {
        if (userId == "") {
            Toast.makeText(applicationContext, resources.getText(R.string.emptyError), Toast.LENGTH_SHORT).show()
            return false
        } else if(!userId.isDigitsOnly()) {
            Toast.makeText(applicationContext, resources.getText(R.string.digitError), Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    fun goToMainProfile() {
        NOTIFY_ALL_ADAPTERS()
        val i = Intent(this@MainActivity, MainProfileActivity::class.java)
        startActivity(i)
    }

    override fun onResume() {
        super.onResume()
        continueCheck["followedUser"] = false
        continueCheck["followedGame"] = false
        continueCheck["games"] = false
        continueCheck["users"] = false
        step1()
    }
}
