package com.android.urbannavigator.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ulasan(
    val ulasanId: String,
    val waktu: String,
    val rating: Int,
    val judul: String,
    val komen: String,
    val userId: String,
    val tamanId: String
): Parcelable