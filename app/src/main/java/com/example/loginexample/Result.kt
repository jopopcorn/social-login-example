package com.example.loginexample

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("resultcode") val resultcode: String,
    @SerializedName("message") val message: String,
    @SerializedName("response") val response: NaverUser
)

data class NaverUser(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String
)
