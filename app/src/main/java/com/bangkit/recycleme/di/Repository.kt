package com.bangkit.recycleme.di

import android.app.Application
import androidx.lifecycle.LiveData
import com.bangkit.recycleme.database.UserDao
import com.bangkit.recycleme.models.Article

class Repository(private val application: Application, private val userDao: UserDao) {

    fun getFavoriteListLiveData(): LiveData<List<Article?>> {
        return userDao.getFavoriteListLiveData()
    }

    suspend fun insertFavoriteUser(user: Article) {
        userDao.insertFavoriteUser(user)
    }

    suspend fun deleteFavoriteUser(user: Article) {
        userDao.deleteFavoriteUser(user)
    }

    suspend fun getFavoriteStatus(userId: String): Boolean? {
        return userDao.getFavoriteStatus(userId)
    }
}