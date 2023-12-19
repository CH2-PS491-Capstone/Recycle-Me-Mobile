package com.bangkit.recycleme.di

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.recycleme.adapter.ArticlePagingSource
import com.bangkit.recycleme.api.ApiService
import com.bangkit.recycleme.models.ListArticlesItem
import com.bangkit.recycleme.models.UserModel
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(private val userPreference: UserPreference, private val apiService: ApiService) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getArticle(): LiveData<PagingData<ListArticlesItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                ArticlePagingSource(apiService)
            }
        ).liveData
    }


    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}