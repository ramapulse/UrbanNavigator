package com.android.urbannavigator.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val postId: String = "",
    val userId: String = "",
    val tamanId: String = "",
    val penyukaPost: List<String> = listOf(),
    val urlFoto: String = "",
    val deskripsi: String = "",
    val waktu: Long = 0
): Parcelable