package com.example.m19_location.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class SinglePhoto(
    @PrimaryKey
    val savedUri : String,

)
