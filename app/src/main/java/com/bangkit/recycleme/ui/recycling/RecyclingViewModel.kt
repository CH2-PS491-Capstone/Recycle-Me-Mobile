package com.bangkit.recycleme.ui.recycling

import androidx.lifecycle.ViewModel
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.RecyclingAddResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RecyclingViewModel(repository: UserRepository) : ViewModel() {

    fun uploadImage(
        barang: String,
        kategori: String,
        recycling: String,
        description: String,
        imageFile: File,
        token: String,
        onImageUploadComplete: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val barangReq = barang.toRequestBody("text/plain".toMediaType())
        val kategoriReq = kategori.toRequestBody("text/plain".toMediaType())
        val recyclingReq = recycling.toRequestBody("text/plain".toMediaType())
        val descriptionReq = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

        val client = ApiConfig.getApiService(token).uploadRecycling(multipartBody, barangReq, kategoriReq, recyclingReq, descriptionReq)
        client.enqueue(object : Callback<RecyclingAddResponse> {
            override fun onResponse(call: Call<RecyclingAddResponse>, response: Response<RecyclingAddResponse>) {
                if (response.isSuccessful) {
                    response.body()?.message?.let { onImageUploadComplete(it) }
                } else {
                    val errorMessage = "Error: ${response.code()} ${response.message()}"
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<RecyclingAddResponse>, t: Throwable) {
                onError("Network call failed: ${t.message}")
            }
        })
    }
}