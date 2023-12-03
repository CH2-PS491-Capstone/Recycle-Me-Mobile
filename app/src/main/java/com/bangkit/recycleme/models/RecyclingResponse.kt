package com.bangkit.recycleme.models

import com.google.gson.annotations.SerializedName

data class RecyclingResponse(

	@field:SerializedName("listRecycling")
	val listRecycling: List<ListRecyclingItem>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListRecyclingItem(

	@field:SerializedName("barang")
	val barang: String? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("coins")
	val coins: Int? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("recycling")
	val recycling: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)
