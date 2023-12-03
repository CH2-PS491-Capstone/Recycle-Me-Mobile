package com.bangkit.recycleme.models

import com.google.gson.annotations.SerializedName

data class RecyclingAddResponse(
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("coins")
    val coins: Int? = 0,

    @field:SerializedName("message")
    val message: String? = null
)
