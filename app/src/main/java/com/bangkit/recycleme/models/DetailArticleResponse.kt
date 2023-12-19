package com.bangkit.recycleme.models

import com.google.gson.annotations.SerializedName

data class DetailArticleResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("article")
	val article: Article? = null
)

data class Article(

	@field:SerializedName("langkah")
	val langkah: String? = null,

	@field:SerializedName("alatBahan")
	val alatBahan: String? = null,

	@field:SerializedName("jenis")
	val jenis: String? = null,

	@field:SerializedName("sumberArtikel")
	val sumberArtikel: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("judul")
	val judul: String? = null,

	@field:SerializedName("gambar")
	val gambar: String? = null,

	@field:SerializedName("sumberGambar")
	val sumberGambar: String? = null
)
