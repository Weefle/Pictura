package com.example.roomexample.models

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPicture(picture: Picture)

    @Query("SELECT * FROM pictures_db")
    fun getPictures(): Flow<List<Picture>>?

    @Update
    suspend fun updatePicture(picture: Picture)

    @Delete
    suspend fun deletePicture(picture: Picture)

}