package com.bangkit.recycleme.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bangkit.recycleme.api.ApiConfig
import com.bangkit.recycleme.api.ApiService
import com.bangkit.recycleme.models.ListArticlesItem
import com.google.android.gms.common.util.CollectionUtils

//class ArticlePagingSource(private val apiService: ApiService) : PagingSource<Int, ListArticlesItem>() {
//    private lateinit var token: String
//    private companion object {
//        const val INITIAL_PAGE_INDEX = 1
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListArticlesItem> {
//        return try {
//            val page = params.key ?: INITIAL_PAGE_INDEX
//            val responseData = ApiConfig.getApiService("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI2d0ZyZlI3dkg1RjNDb2N2NFVSQSIsImVtYWlsIjoibWF1bG1hdWxAZ21haWwuY29tIiwiaWF0IjoxNzAxNTgwODY4LCJleHAiOjE3MDQxNzI4Njh9.IQtZiVnCny77YvRkg42b63yTxCdE5gSuDE60wcsig-M").getArticle(page, params.loadSize)
//
//            return LoadResult.Page(
//                data = responseData.listArticles,
//                prevKey = if (page == 1) null else page - 1,
//                nextKey = if (CollectionUtils.isEmpty(responseData.listArticles)) null else page + 1
//            )
//        } catch (exception: Exception) {
//            return LoadResult.Error(exception)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, ListArticlesItem>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//}