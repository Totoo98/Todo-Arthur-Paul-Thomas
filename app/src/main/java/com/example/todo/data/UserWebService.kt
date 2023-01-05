package com.example.todo.data

import android.graphics.Bitmap
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File

interface UserWebService {
    @GET("/sync/v9/user/")
    suspend fun fetchUser(): Response<User>
    @Multipart
    @POST("sync/v9/update_avatar")
    suspend fun updateAvatar(@Part avatar: MultipartBody.Part): Response<User>
    /*@PATCH("sync/v9/sync")
    suspend fun update(@Body user: UserUpdate): Response<Unit>*/
}