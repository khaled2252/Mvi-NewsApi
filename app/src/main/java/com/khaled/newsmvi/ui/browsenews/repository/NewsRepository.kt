package com.khaled.newsmvi.ui.browsenews.repository

import com.khaled.newsmvi.data.api.NewsApi
import javax.inject.Inject

class NewsRepository @Inject constructor() {
    @Inject
    lateinit var apiService: NewsApi

    fun getNewsResult(country: String, page: String) = apiService.getNewsList(country = country, page = page)
}