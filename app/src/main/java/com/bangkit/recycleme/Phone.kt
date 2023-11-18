package com.bangkit.recycleme

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Phone(
    val name: String,
    val description: String,
    val detailDescription: String,
    val photo: Int,
    val price: String
) : Parcelable
