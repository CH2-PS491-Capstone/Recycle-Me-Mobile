package com.bangkit.recycleme.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.recycleme.di.UserRepository
import com.bangkit.recycleme.models.ListArticlesItem

class ArticleViewModel(repository: UserRepository): ViewModel(){
    val error = MutableLiveData<String>()

    val article: LiveData<PagingData<ListArticlesItem>> =
        repository.getArticle().cachedIn(viewModelScope)
}