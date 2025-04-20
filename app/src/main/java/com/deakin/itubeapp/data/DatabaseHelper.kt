package com.deakin.itubeapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.deakin.itubeapp.model.User
import com.deakin.itubeapp.util.Util

class DatabaseHelper(
    context: Context?,
    factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, Util.DATABASE_NAME, factory, Util.DATABASE_VERSION) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val CREATE_USER_TABLE = """
            CREATE TABLE ${Util.USER_TABLE_NAME} (
                ${Util.USER_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Util.USERNAME} TEXT,
                ${Util.PASSWORD} TEXT
            )
        """.trimIndent()

        val CREATE_PLAYLIST_TABLE = """
            CREATE TABLE ${Util.PLAYLIST_TABLE_NAME} (
                ${Util.PLAYLIST_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Util.USER_ID} INTEGER NOT NULL,
                ${Util.VIDEO_ID} TEXT,
                FOREIGN KEY (${Util.USER_ID}) REFERENCES ${Util.USER_TABLE_NAME}(${Util.USER_ID})
            )
        """.trimIndent()

        sqLiteDatabase.execSQL(CREATE_USER_TABLE)
        sqLiteDatabase.execSQL(CREATE_PLAYLIST_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_USER_TABLE = "DROP TABLE IF EXISTS ${Util.USER_TABLE_NAME}"
        val DROP_PLAYLIST_TABLE = "DROP TABLE IF EXISTS ${Util.PLAYLIST_TABLE_NAME}"
        sqLiteDatabase.execSQL(DROP_USER_TABLE)
        sqLiteDatabase.execSQL(DROP_PLAYLIST_TABLE)

        onCreate(sqLiteDatabase)
    }

    fun userAlreadyExists(username: String): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var userExists = false

        try {
            cursor = db.query(
                Util.USER_TABLE_NAME,
                arrayOf(Util.USER_ID),
                "${Util.USERNAME} = ?",
                arrayOf(username),
                null,
                null,
                null
            )

            userExists = cursor.count > 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }

        return userExists
    }

    fun insertUser(user: User): Long {
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues: ContentValues = ContentValues().apply {
            put(Util.USERNAME, user.username)
            put(Util.PASSWORD, user.password)
        }

        val newRowId: Long = db.insert(
            /* table = */ Util.USER_TABLE_NAME,
            /* nullColumnHack = */ null,
            /* values = */ contentValues)
        db.close()

        return newRowId
    }

    fun fetchUser(username: String, password: String): Int {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var userId = -1

        try {
            cursor = db.query(
                Util.USER_TABLE_NAME,
                arrayOf(Util.USER_ID),
                "${Util.USERNAME} = ? AND ${Util.PASSWORD} = ?",
                arrayOf(username, password),
                null,
                null,
                null
            )

            if (cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(Util.USER_ID))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }

        return userId
    }

    fun insertVideoToPlaylist(userId: String, videoId: String): Long {
        val db: SQLiteDatabase = this.writableDatabase
        val contentValues: ContentValues = ContentValues().apply {
            put(Util.USER_ID, userId)
            put(Util.VIDEO_ID, videoId)
        }

        val newRowId: Long = db.insert(
            /* table = */ Util.PLAYLIST_TABLE_NAME,
            /* nullColumnHack = */ null,
            /* values = */ contentValues)
        db.close()

        return newRowId
    }

    fun checkVideoExists(userId: String, videoId: String): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var videoExists = false

        try {
            cursor = db.query(
                /* table = */ Util.PLAYLIST_TABLE_NAME,
                /* columns = */ arrayOf(Util.PLAYLIST_ID),
                /* selection = */ "${Util.VIDEO_ID} = ? AND ${Util.USER_ID}",
                /* selectionArgs = */ arrayOf(videoId, userId),
                /* groupBy = */ null,
                /* having = */ null,
                /* orderBy = */ null
            )

            videoExists = cursor.count > 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }

        return videoExists
    }
}
