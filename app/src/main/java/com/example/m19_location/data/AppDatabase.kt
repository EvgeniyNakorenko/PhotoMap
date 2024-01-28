package com.example.m19_location.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SinglePhoto::class,

    ],
    version = 1,
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}