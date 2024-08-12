package com.android.urbannavigator.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Komentar(
    val komentarId: String = "",
    val waktu: Long = 0,
    val komen: String = "",
    val userId: String = "",
    val postId: String = ""
): Parcelable