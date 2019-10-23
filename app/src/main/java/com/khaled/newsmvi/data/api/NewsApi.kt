package com.khaled.newsmvi.data.api

import com.khaled.newsmvi.data.models.News
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("/v2/top-headlines")
    fun getNewsList(@Query("country") country : String ,@Query("apiKey") apiKey : String = API_KEY): Single<News>

    companion object {
        const val API_KEY = "a06f0d03691a47459237b156a1dc7bd5"
    }
}