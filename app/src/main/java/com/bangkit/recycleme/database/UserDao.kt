package com.bangkit.recycleme.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangkit.recycleme.models.Article

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteUser(user: Article)

    @Delete
    suspend fun deleteFavoriteUser(user: Article)

    @Query("SELECT isFavorite FROM article WHERE id = :userId")
    suspend fun getFavoriteStatus(userId: String): Boolean?

    @Query("SELECT * FROM article ORDER BY judul ASC")
    fun getFavoriteListLiveData(): LiveData<List<Article?>>
}