package com.st.steamy.data.permanent

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.st.steamy.data.storage.News

class SteamyContentProvider : ContentProvider() {

    var db: SQLiteDatabase? = null

    companion object {
        val CONTENT_AUTHORITY = "st.com.steamy.steamyInfo"
        val PATH_GAME = "game"
        val PATH_USER = "user"
        val PATH_FOLLOWEDGAME = "followedgame"
        val PATH_FOLLOWEDUSER = "followeduser"
        val PATH_NEWS = "news"
        val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")
        val GAME_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GAME)
        val USER_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USER)
        val FOLLOWEDGAME_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FOLLOWEDGAME)
        val FOLLOWEDUSER_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FOLLOWEDUSER)
        val NEWS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_NEWS)

        val matcher: UriMatcher

        init {
            matcher = UriMatcher(UriMatcher.NO_MATCH)
            matcher.addURI(CONTENT_AUTHORITY, PATH_GAME, 100)
            matcher.addURI(CONTENT_AUTHORITY, PATH_USER, 200)
            matcher.addURI(CONTENT_AUTHORITY, PATH_FOLLOWEDGAME, 300)
            matcher.addURI(CONTENT_AUTHORITY, PATH_FOLLOWEDUSER, 400)
            matcher.addURI(CONTENT_AUTHORITY, PATH_NEWS, 500)
        }


    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when(matcher.match(uri)) {
            100 -> { // Game
                return db?.delete(SteamyContract.GameEntry.TABLE_NAME, selection, selectionArgs) ?: 0
            }
            200 -> { // User
                return db?.delete(SteamyContract.UserEntry.TABLE_NAME, selection, selectionArgs) ?: 0
            }
            300 -> { // Followed Game
                return db?.delete(SteamyContract.FollowedGameEntry.TABLE_NAME, selection, selectionArgs) ?: 0
            }
            400 -> { // Followed User
                return db?.delete(SteamyContract.FollowedUserEntry.TABLE_NAME, selection, selectionArgs) ?: 0
            }
            500 -> { // News
                return db?.delete(SteamyContract.NewsEntry.TABLE_NAME, selection, selectionArgs) ?: 0
            }
        }

        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when(matcher.match(uri)) {
            100 -> { // Game
                val rank = db?.insert(SteamyContract.GameEntry.TABLE_NAME, null, values)
                if (rank!=null && rank>0)
                    return ContentUris.withAppendedId(GAME_URI, rank)
            }
            200 -> { // User
                val rank = db?.insert(SteamyContract.UserEntry.TABLE_NAME, null, values)
                if (rank!=null && rank>0)
                    return ContentUris.withAppendedId(USER_URI, rank)
            }
            300 -> { // Followed Game
                val rank = db?.insert(SteamyContract.FollowedGameEntry.TABLE_NAME, null, values)
                if (rank!=null && rank>0)
                    return ContentUris.withAppendedId(FOLLOWEDGAME_URI, rank)
            }
            400 -> { // Followed User
                val rank = db?.insert(SteamyContract.FollowedUserEntry.TABLE_NAME, null, values)
                if (rank!=null && rank>0)
                    return ContentUris.withAppendedId(FOLLOWEDUSER_URI, rank)
            }
            500 -> { // News
                val rank = db?.insert(SteamyContract.NewsEntry.TABLE_NAME, null, values)
                if (rank!=null && rank>0)
                    return ContentUris.withAppendedId(NEWS_URI, rank)
            }
        }

        return null
    }

    override fun onCreate(): Boolean {
        val helper = DataBaseHelper(context!!)
        db = helper.writableDatabase
        return db == null
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        when(matcher.match(uri)) {
            100 -> { // Game
                return db?.query(SteamyContract.GameEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
            }
            200 -> { // User
                return db?.query(SteamyContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
            }
            300 -> { // Followed Game
                return db?.query(SteamyContract.FollowedGameEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
            }
            400 -> { // Followed User
                return db?.query(SteamyContract.FollowedUserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
            }
            500 -> { // News
                return db?.query(SteamyContract.NewsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
            }
        }
        return null
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when(matcher.match(uri)) {
            100 -> { // Game
                return db?.update(SteamyContract.GameEntry.TABLE_NAME, values, selection, selectionArgs) ?: 0
            }
            200 -> { // User
                return db?.update(SteamyContract.UserEntry.TABLE_NAME, values, selection, selectionArgs) ?: 0
            }
            300 -> { // Followed Game
                return db?.update(SteamyContract.FollowedGameEntry.TABLE_NAME, values, selection, selectionArgs) ?: 0
            }
            400 -> { // Followed User
                return db?.update(SteamyContract.FollowedUserEntry.TABLE_NAME, values, selection, selectionArgs) ?: 0
            }
            500 -> { // News
                return db?.update(SteamyContract.NewsEntry.TABLE_NAME, values, selection, selectionArgs) ?: 0
            }
        }
        return 0

    }

    class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, SteamyContract.DATABASE_NAME, null, SteamyContract.DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL(SteamyContract.FollowedGameEntry.CREATE_TABLE)
            db?.execSQL(SteamyContract.FollowedUserEntry.CREATE_TABLE)
            db?.execSQL(SteamyContract.GameEntry.CREATE_TABLE)
            db?.execSQL(SteamyContract.UserEntry.CREATE_TABLE)
            db?.execSQL(SteamyContract.NewsEntry.CREATE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS ${SteamyContract.FollowedGameEntry.TABLE_NAME}")
            db?.execSQL("DROP TABLE IF EXISTS ${SteamyContract.FollowedUserEntry.TABLE_NAME}")
            db?.execSQL("DROP TABLE IF EXISTS ${SteamyContract.GameEntry.TABLE_NAME}")
            db?.execSQL("DROP TABLE IF EXISTS ${SteamyContract.UserEntry.TABLE_NAME}")
            db?.execSQL("DROP TABLE IF EXISTS ${SteamyContract.NewsEntry.TABLE_NAME}")
            onCreate(db)
        }

    }


}
