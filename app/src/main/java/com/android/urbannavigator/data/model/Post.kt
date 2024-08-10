package com.android.urbannavigator.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val postId: String,
    val userId: String,
    val tamanId: String,
    val penyukaPost: List<String>,
    val urlFoto: String,
    val deskripsi: String,
    val waktu: String
): Parcelable