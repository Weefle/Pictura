package com.example.roomexample.API

import com.example.roomexample.models.PictureSearch
import com.example.roomexample.models.PicturesItem
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("photos/?per_page=50")
    suspend fun getPictures(@Header("Authorization") token: String, @Query("page") page : Int?=1): Result<MutableList<PicturesItem>?>

    @GET("search/photos/?per_page=50")
    suspend fun getPicturesFromQuery(@Header("Authorization") token: String, @Query("query") query : String?, @Query("page") page : Int?=1, @Query("lang") lang : String?="fr"): Result<PictureSearch?>

    /*@GET("photos/{id}")
    suspend fun getPictureById(@Header("Authorization") token: String, @Path("id") id : String?): Result<PicturesItem?>*/

    @POST("photos/{id}/like")
    suspend fun likePictureById(@Header("Authorization") token: String, @Path("id") id : String?): Result<PicturesItem?>

    /*@GET("users/weefle/likes")
    suspend fun getPicturesLiked(@Header("Authorization") token: String): Result<MutableList<PicturesItem>?>*/

    @DELETE("photos/{id}/like")
    suspend fun dislikePictureById(@Header("Authorization") token: String, @Path("id") id : String?): Result<PicturesItem?>
}