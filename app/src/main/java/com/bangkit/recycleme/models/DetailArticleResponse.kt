package com.bangkit.recycleme.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class DetailArticleResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("article")
	val article: Article? = null
)

@Entity
@Parcelize
data class Article(

	@field:SerializedName("id")
	@ColumnInfo(name = "id")
	@PrimaryKey
	val id: String = "",

	@field:SerializedName("langkah")
	@ColumnInfo(name = "langkah")
	val langkah: String? = "",

	@field:SerializedName("alatBahan")
	@ColumnInfo(name = "alatBahan")
	val alatBahan: String? = "",

	@field:SerializedName("jenis")
	@ColumnInfo(name = "jenis")
	val jenis: String? = "",

	@field:SerializedName("sumberArtikel")
	@ColumnInfo(name = "sumberArtikel")
	val sumberArtikel: String? = "",

	@field:SerializedName("judul")
	@ColumnInfo(name = "judul")
	val judul: String? = "",

	@field:SerializedName("gambar")
	@ColumnInfo(name = "gambar")
	val gambar: String? = "",

	@field:SerializedName("sumberGambar")
	@ColumnInfo(name = "sumberGambar")
	val sumberGambar: String? = "",

	@ColumnInfo(name = "isFavorite")
	var isFavorite: Boolean? = false
) : Parcelable
