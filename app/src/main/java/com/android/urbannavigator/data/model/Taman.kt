package com.android.urbannavigator.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Taman(
    val tamanId: String = "",
    val listGambar: List<String> = listOf(),
    val nama: String = "",
    val hariBuka: String = "",
    val sampaiHariBuka: String = "",
    val jamBuka: String = "",
    val jamTutup: String = "",
    val deskripsi: String = "",
    val alamat: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0
): Parcelable