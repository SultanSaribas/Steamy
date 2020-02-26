package com.st.steamy.data.permanent

import android.provider.BaseColumns


class SteamyContract {

    companion object {
        val DATABASE_NAME = "steamyInfo"
        val DATABASE_VERSION = 5
    }

    class FollowedGameEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "main_user_followed_games"
            val COLUMN_STEAMID = "steamId"
            val CREATE_TABLE = """ CREATE TABLE $TABLE_NAME (
                $COLUMN_STEAMID INTEGER,
                UNIQUE($COLUMN_STEAMID) ON CONFLICT REPLACE)""".trimIndent()
        }
    }

    class FollowedUserEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "main_user_followed_users"
            val COLUMN_STEAMID = "steamId"
            val CREATE_TABLE = """ CREATE TABLE $TABLE_NAME (
                $COLUMN_STEAMID INTEGER,
                UNIQUE($COLUMN_STEAMID) ON CONFLICT REPLACE)""".trimIndent()
        }
    }

    class GameEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "games"
            val COLUMN_STEAMID = "steamId"
            val COLUMN_NAME = "name"
            val COLUMN_LOGO = "logo"
            val CREATE_TABLE = """ CREATE TABLE $TABLE_NAME (
                $COLUMN_STEAMID INTEGER,
                $COLUMN_NAME TEXT,
                $COLUMN_LOGO TEXT,
                UNIQUE($COLUMN_STEAMID) ON CONFLICT REPLACE)""".trimIndent()
        }
    }

    class UserEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "users"
            val COLUMN_STEAMID = "steamId"
            val COLUMN_NAME = "name"
            val COLUMN_PHOTO = "photo"
            val CREATE_TABLE = """ CREATE TABLE $TABLE_NAME (
                $COLUMN_STEAMID INTEGER,
                $COLUMN_NAME TEXT,
                $COLUMN_PHOTO TEXT,
                UNIQUE($COLUMN_STEAMID) ON CONFLICT REPLACE)""".trimIndent()
        }
    }

    class NewsEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "news"
            val COLUMN_NEWS_ID = "newsId"
            val COLUMN_GAME_STEAMID = "gameId"
            val COLUMN_TITLE = "title"
            val COLUMN_CONTENT = "content"
            val CREATE_TABLE = """ CREATE TABLE $TABLE_NAME (
                $COLUMN_NEWS_ID INTEGER,
                $COLUMN_GAME_STEAMID INTEGER,
                $COLUMN_TITLE TEXT,
                $COLUMN_CONTENT TEXT,
                UNIQUE($COLUMN_NEWS_ID) ON CONFLICT IGNORE) """.trimIndent()
        }
    }

}
