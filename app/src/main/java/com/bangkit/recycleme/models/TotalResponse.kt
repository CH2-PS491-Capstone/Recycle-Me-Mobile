package com.bangkit.recycleme.models

import com.google.gson.annotations.SerializedName

data class TotalResponse(
	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("listRecycling")
	val listRecycling: Int? = null,

	@field:SerializedName("coins")
	val coins: Int? = null
)
