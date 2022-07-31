package com.example.tauconnect

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class UserDatabase(context: Context): SQLiteOpenHelper(context, databaseName, null, version) {

    companion object{
        private const val databaseName = "user_db"
        private const val version = 1


        private const val tableName = "user_tbl"
        private const val name = "name"
        private const val profilePicture = "profile_pic"
        private const val userType = "user_type"
        private const val email = "email"
        private const val token = "token"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE $tableName($name TEXT, $profilePicture TEXT, $userType TEXT, $email TEXT, $token TEXT)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }


    fun add(loginDetails: LoginDetails){
        val db = this.writableDatabase

        val content = ContentValues()
        content.put(name,loginDetails.user.name)
        content.put(profilePicture, loginDetails.user.profile_picture)
        content.put(userType, loginDetails.user.user_type)
        content.put(email, loginDetails.user.email)
        content.put(token, loginDetails.token)

        db.insert(tableName, null, content)
        db.close()
    }

    @SuppressLint("Range")
    fun getToken(): String{
        val db = this.readableDatabase
        val cursor: Cursor
        val query = "SELECT * FROM $tableName"

        var stoken = ""
        try{
            cursor = db.rawQuery(query, null)
        }catch (e: SQLiteException){
            db.execSQL(query)
            return stoken
        }


        if(cursor.moveToFirst()){
            stoken = cursor.getString(cursor.getColumnIndex(token))
        }

        return stoken
    }
    @SuppressLint("Range")
    fun getUserType(): String{
        val db = this.readableDatabase
        val cursor: Cursor
        val query = "SELECT * FROM $tableName"

        var stoken = ""
        try{
            cursor = db.rawQuery(query, null)
        }catch (e: SQLiteException){
            db.execSQL(query)
            return stoken
        }


        if(cursor.moveToFirst()){
            stoken = cursor.getString(cursor.getColumnIndex(userType))
        }

        return stoken
    }

    fun getSize(): Int{
        val db = this.readableDatabase
        val cursor: Cursor
        val query = "SELECT * FROM $tableName"

        var size = 0
        try{
            cursor = db.rawQuery(query, null)
        }catch (e: SQLiteException){
            db.execSQL(query)
            return size
        }


        if(cursor.moveToFirst()){
            size++
        }

        return size

    }

    fun deleteAll(){
        val db = this.writableDatabase
        db.delete(tableName, null, null)
        db.close()
    }
}