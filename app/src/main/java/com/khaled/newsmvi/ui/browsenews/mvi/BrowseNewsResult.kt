package com.khaled.newsmvi.ui.browsenews.mvi

import com.khaled.newsmvi.mvibase.MviResult
import com.khaled.newsmvi.data.models.News


sealed class BrowseNewsResult : MviResult {
    sealed class PopulateTaskResult : BrowseNewsResult() {
        data class Success(val news : News) : PopulateTaskResult()
        data class Failure(val error: Throwable) : PopulateTaskResult()
        object InFlight : PopulateTaskResult()
    }
}
