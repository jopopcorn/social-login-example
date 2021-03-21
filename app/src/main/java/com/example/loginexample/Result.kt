package com.example.loginexample

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Result(
    val resultcode: String,
    val message: String,
    val response: NaverUser
) : Parcelable

@Parcelize
data class NaverUser(
    val id: String,
    val email: String
) : Parcelable
