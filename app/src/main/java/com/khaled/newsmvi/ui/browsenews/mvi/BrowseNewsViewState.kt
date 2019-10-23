package com.khaled.newsmvi.ui.browsenews.mvi

import com.khaled.newsmvi.mvibase.MviViewState
import com.khaled.newsmvi.data.models.Article

data class BrowseNewsViewState(
        val isLoaded: Boolean,
        val error: Throwable?,
        val newsList : ArrayList<Article>
) : MviViewState {
    companion object {
        fun idle(): BrowseNewsViewState {
            return BrowseNewsViewState(
                    isLoaded = false,
                    error = null,
                    newsList = ArrayList()
            )
        }
    }
}
