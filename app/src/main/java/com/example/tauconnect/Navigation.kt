package com.example.tauconnect

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class Navigation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val navigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val controller = findNavController(R.id.fragmentContainerView)
        navigationView.setupWithNavController(controller)
        val db = UserDatabase(this)
        val token = db.getToken()
        var notifid = 1
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                val notifications = try {
                    RetrofitInstance.retro.getNotifications("Bearer $token")
                } catch (e: Exception) {
                    Log.e("notifications", e.toString())
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    for(i in notifications.indices) {
                        val notifBuilder = NotificationCompat.Builder(this@Navigation,"My Notification")
                        notifBuilder.setContentTitle(notifications[i].title)
                        notifBuilder.setContentText(notifications[i].message)
                        notifBuilder.setSmallIcon(R.drawable.tau_logo)
                        notifBuilder.setAutoCancel(true)

                        val managerCompat = NotificationManagerCompat.from(this@Navigation)
                        managerCompat.notify(notifid, notifBuilder.build())
                        notifid++
                    }
                }
                delay(5000)
            }
        }
    }
}