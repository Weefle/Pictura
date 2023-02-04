package com.example.roomexample.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pictures_db")
data class Picture(
@PrimaryKey
@ColumnInfo(name = "id")
val id: String,
@ColumnInfo(name = "blob")
val blob: ByteArray?,
@ColumnInfo(name = "author")
val author: String?,
@ColumnInfo(name = "description")
val description: String?,
@ColumnInfo(name = "date")
val date: String?,
@ColumnInfo(name = "insta")
val insta: String?
)
