package com.bangkit.recycleme.api

import com.bangkit.recycleme.models.AddFavoriteResponse
import com.bangkit.recycleme.models.ArticleResponse
import com.bangkit.recycleme.models.DeleteFavoriteResponse
import com.bangkit.recycleme.models.DeleteResponse
import com.bangkit.recycleme.models.DetailArticleResponse
import com.bangkit.recycleme.models.DetailResponse
import com.bangkit.recycleme.models.FavoriteArticlesItem
import com.bangkit.recycleme.models.GetFavoriteArticleResponse
import com.bangkit.recycleme.models.LoginResponse
import com.bangkit.recycleme.models.RandomArticleResponse
import com.bangkit.recycleme.models.RecyclingAddResponse
import com.bangkit.recycleme.models.RecyclingResponse
import com.bangkit.recycleme.models.RegisterResponse
import com.bangkit.recycleme.models.RekomendasiArtikelResponse
import com.bangkit.recycleme.models.TotalResponse
import com.bangkit.recycleme.models.WithdrawResponse
import com.bangkit.recycleme.ui.recyclingresult.RecyclingResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class WithdrawRequest(
    val inputCoins: Int,
)

data class AddFavoriteRequest(
    val id: String,
)

interface ApiService {
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Multipart
    @POST("recycling/add")
    fun uploadRecycling(
        @Part file: MultipartBody.Part,
        @Part("barang") barang: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("recycling") recycling: RequestBody,
        @Part("description") description: RequestBody
    ): Call<RecyclingAddResponse>

    @GET("recycling/results")
    fun getRecycling(): Call<RecyclingResponse>

    @GET("recycling/results/{id}")
    fun detailRecycling(
        @Path("id") id: String,
    ): Call<DetailResponse>

    @GET("user/coins")
    fun getTotal(): Call<TotalResponse>

    @PUT("user/coins")
    fun withdrawCoin(@Body request: WithdrawRequest): Call<WithdrawResponse>

    @DELETE("recycling/delete/{id}")
    fun deleteRecycling(
        @Path("id") id: String
    ): Call<DeleteResponse>

    @GET("articles")
    fun getArticle(
        @Query("search") search: String = "",
        @Query("jenis") jenis: String = ""
    ): Call<ArticleResponse>

    @GET("articles/{id}")
    fun getDetailArticle(
        @Path("id") id: String,
    ): Call<DetailArticleResponse>

    @POST("user/favorite")
    fun addArticleFavorite(@Body request: AddFavoriteRequest): Call<AddFavoriteResponse>

    @DELETE("user/favorite/{id}")
    fun deleteFavoriteArticle(
        @Path("id") id: String
    ): Call<DeleteFavoriteResponse>

    @GET("user/favorite")
    fun getFavoriteArticle(): Call<GetFavoriteArticleResponse>

    @GET("articles/random")
    fun getArtikelRekomendasi(): Call<RandomArticleResponse>

    @GET("articles/recommendation/{judul}")
    fun getRekomendasiArtikel(
        @Path("judul") judul: String,
    ): Call<RekomendasiArtikelResponse>

    @GET("user/favorite")
    fun judulArtikel(): Call<FavoriteArticlesItem>
}