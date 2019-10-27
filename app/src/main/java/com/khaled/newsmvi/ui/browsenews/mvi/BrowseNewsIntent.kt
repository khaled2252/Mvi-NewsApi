package com.khaled.newsmvi.ui.browsenews.mvi

import com.khaled.newsmvi.mvibase.MviIntent

sealed class BrowseNewsIntent : MviIntent {
    object InitialIntent : BrowseNewsIntent()
    data class LoadMoreIntent(val page : Int) : BrowseNewsIntent()
}
