package com.silverbullet.loadapp.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

@Suppress("unused")
interface RetrofitApi {

    @GET("bumptech/glide")
    suspend fun getGlidePage(): Response<ResponseBody>

    @GET("udacity/nd940-c3-advanced-android-programming-project-starter")
    suspend fun getStarterProjectPage(): Response<ResponseBody>

    @GET("/square/retrofit")
    suspend fun getRetrofitPage(): Response<ResponseBody>

    companion object {

        val instance by lazy { createAPI() }

        private const val BASE_URL = "https://github.com/"

        private fun createAPI(): RetrofitApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitApi::class.java)
        }
    }
}