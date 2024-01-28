package com.example.m19_location.data

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.google.firebase.crashlytics.FirebaseCrashlytics

class App : Application() {

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()

        db = Room.inMemoryDatabaseBuilder(
            applicationContext,
            AppDatabase::class.java,
        ).fallbackToDestructiveMigration()
            .build()

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel()

    }
    companion object {
        const val Notification_Channel_ID = "test_channel_id"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(){
        val name = "Test notificationChannel"
        val descriptionText = "This is a simple description"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(Notification_Channel_ID,name, importance).apply {
            description = descriptionText
        }

        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}