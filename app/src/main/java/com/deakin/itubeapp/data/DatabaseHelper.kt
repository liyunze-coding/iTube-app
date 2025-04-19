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
            CREATE TABLE ${Util.TABLE_NAME} (
                ${Util.USER_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Util.USERNAME} TEXT,
                ${Util.PASSWORD} TEXT
            )
        """.trimIndent()

        sqLiteDatabase.execSQL(CREATE_USER_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_USER_TABLE = "DROP TABLE IF EXISTS ${Util.TABLE_NAME}"
        sqLiteDatabase.execSQL(DROP_USER_TABLE)

        onCreate(sqLiteDatabase)
    }

    fun userAlreadyExists(username: String): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var userExists = false

        try {
            cursor = db.query(
                Util.TABLE_NAME,
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
            /* table = */ Util.TABLE_NAME,
            /* nullColumnHack = */ null,
            /* values = */ contentValues)
        db.close()

        return newRowId
    }

    fun fetchUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var userExists = false

        try {
            cursor = db.query(
                Util.TABLE_NAME,
                arrayOf(Util.USER_ID),
                "${Util.USERNAME} = ? AND ${Util.PASSWORD} = ?",
                arrayOf(username, password),
                null,
                null,
                null
            )
//            val cursor = db.rawQuery(
//                "SELECT * FROM ${Util.TABLE_NAME} WHERE ${Util.USERNAME} = ? AND ${Util.PASSWORD} = ?",
//                arrayOf(username, password)
//            )

            userExists = cursor.count > 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }

        return userExists
    }

}
