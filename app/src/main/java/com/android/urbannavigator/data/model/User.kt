package com.android.urbannavigator.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val profilePic: String = "",
    val username: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val location: String = "",
    val email: String = "",
    val uid: String = "",
    val notification: Boolean? = null
): Parcelable