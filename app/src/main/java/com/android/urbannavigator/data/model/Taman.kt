package com.android.urbannavigator.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Taman(
    val tamanId: String,
    val listGambar: List<String>,
    val nama: String,
    val jamBuka: String,
    val jamTutup: String,
    val deskripsi: String,
    val alamat: String,
    val lat: Double,
    val lon: Double
): Parcelable