package com.bangkit.recycleme.models

import com.google.gson.annotations.SerializedName

data class DeleteResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
