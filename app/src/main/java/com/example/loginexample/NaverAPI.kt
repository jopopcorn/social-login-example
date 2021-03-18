package com.example.loginexample

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface NaverAPI {
    @GET("v1/nid/me")
    fun getUserInfo(
        @Header("Authorization") authorization: String
    ): Call<Result>
}