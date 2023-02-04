package com.example.roomexample.models

data class PicturesItem(
    val id: String?,
    val blur_hash: String?,
    val color: String?,
    val created_at: String?,
    val description: String?,
    val height: Int?,
    var liked_by_user: Boolean?,
    var likes: Int?,
    val updated_at: String?,
    val width: Int?,
    val urls: Urls?,
    val user: User?
)