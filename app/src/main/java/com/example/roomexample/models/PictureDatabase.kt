package com.example.roomexample.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Picture::class],
    version = 1,
    exportSchema = true
)
abstract class PictureDatabase : RoomDatabase() {

    abstract fun pictureDao(): PictureDao

    companion object {

        @Volatile
        private var INSTANCE: PictureDatabase? = null

        fun getDatabase(context: Context): PictureDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            if (INSTANCE == null) {
                synchronized(this) {
                    // Pass the database to the INSTANCE
                    INSTANCE = buildDatabase(context)
                }
            }
            // Return database.
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): PictureDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                PictureDatabase::class.java,
                "pictures_db"
            )
                .build()
        }
    }
}