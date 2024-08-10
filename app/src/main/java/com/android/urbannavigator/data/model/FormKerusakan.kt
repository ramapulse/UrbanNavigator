package com.android.urbannavigator.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FormKerusakan(
    val formId: String,
    val tamanId: String,
    val userId: String,
    val judul: String,
    val deskripsi: String,
    val tanggal: String
): Parcelable