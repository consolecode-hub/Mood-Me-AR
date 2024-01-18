package com.mood.ar.MyAPI

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MyAPI {

    @Multipart
    @POST("upload")
    fun uploadFILE(
        @Part image: MultipartBody.Part,
        @Part("name") desc: RequestBody,
        @Part("extension") extension: RequestBody,
        @Part("imei") imei: RequestBody
    ): Call<UploadResponse>



    companion object {
        operator fun invoke(): MyAPI {
            return Retrofit.Builder()
                .baseUrl("http://Localhost:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyAPI::class.java)
        }
    }
}