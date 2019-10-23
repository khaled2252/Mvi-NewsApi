package com.khaled.newsmvi.ui.browsenews.mvi

import com.khaled.newsmvi.mvibase.MviAction

sealed class BrowseNewsAction : MviAction {
  data class PopulateNewsAction(val country: String) : BrowseNewsAction()
  object SkipMe : BrowseNewsAction()
}
