package com.example.m19_location.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Query("SELECT*FROM photos ")
    fun getAll(): Flow<List<SinglePhoto>>

    @Insert(
        onConflict = OnConflictStrategy.IGNORE
    )
    suspend fun insert(singlePhoto: SinglePhoto)


}