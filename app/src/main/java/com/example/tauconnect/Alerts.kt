package com.example.tauconnect

import android.app.AlertDialog
import android.content.Context

class Alerts(private val context: Context) {

    fun timeout(){
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage("Connection time out.")
            .setPositiveButton("OK", null)
            .show()
    }

    fun error(errorText: String){
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(errorText)
            .setPositiveButton("OK", null)
            .show()
    }
}