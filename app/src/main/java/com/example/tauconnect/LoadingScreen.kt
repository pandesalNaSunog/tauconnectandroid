package com.example.tauconnect

import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView

class LoadingScreen(private val context: Context) {
    private lateinit var progress: ProgressDialog
    fun showLoadingScreen(text: String){
        progress = ProgressDialog(context)
        val progressView = LayoutInflater.from(context).inflate(R.layout.loading_screen, null)
        val loadingText = progressView.findViewById<TextView>(R.id.loadingText)
        loadingText.text = text
        progress.show()
        progress.setContentView(progressView)
        progress.setCancelable(false)
        progress.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    fun dismiss(){
        progress.dismiss()
    }
}