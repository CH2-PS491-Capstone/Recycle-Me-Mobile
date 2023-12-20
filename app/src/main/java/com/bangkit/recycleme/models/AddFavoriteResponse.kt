package com.bangkit.recycleme.models

import com.google.gson.annotations.SerializedName

data class AddFavoriteResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
