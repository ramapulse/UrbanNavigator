package com.android.urbannavigator.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val eventId: String = "",
    val tamanId: String= "",
    val nama: String= "",
    val deskripsi: String= "",
    val foto: String= "",
    val tanggalMulai: String= "",
    val tanggalSelesai: String= "",
    val jamMulai: String = "",
    val jamSelesai: String= ""
): Parcelable