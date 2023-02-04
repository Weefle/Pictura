package com.example.roomexample.models

data class PictureSearch(
    val results: List<PicturesItem>?,
    val total: Int?,
    val total_pages: Int?
)