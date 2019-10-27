/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.khaled.newsmvi.ui.browsenews.viewmodel

import androidx.lifecycle.ViewModel
import com.khaled.newsmvi.mvibase.MviViewModel
import com.khaled.newsmvi.ui.browsenews.mvi.*
import com.khaled.newsmvi.ui.browsenews.mvi.BrowseNewsAction.PopulateNewsAction
import com.khaled.newsmvi.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Listens to user actions from the UI ([BrowseNewsFragment]), retrieves the data and updates
 * the UI as required.
 *
 * @property actionProcessorHolder Contains and executes the business logic of all emitted actions.
 */
class BrowseNewsViewModel @Inject constructor(
        private val actionProcessorHolder: BrowseNewsActionProcessorHolder
) : ViewModel(), MviViewModel<BrowseNewsIntent, BrowseNewsViewState> {

    /**
     * Proxy subject used to keep the stream alive even after the UI gets recycled.
     * This is basically used to keep ongoing events and the last cached State alive
     * while the UI disconnects and reconnects on config changes.
     */
    private val intentsSubject: PublishSubject<BrowseNewsIntent> = PublishSubject.create()
    private val statesObservable: Observable<BrowseNewsViewState> = compose()
    private val disposables = CompositeDisposable()

    /**
     * take only the first ever InitialIntent and all intents of other types
     * to avoid reloading data on config changes
     */
    private val intentFilter: ObservableTransformer<BrowseNewsIntent, BrowseNewsIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge<BrowseNewsIntent>(
                        shared.ofType(BrowseNewsIntent.InitialIntent::class.java).take(1),
                        shared.notOfType(BrowseNewsIntent.InitialIntent::class.java)
                )
            }
        }

    override fun processIntents(intents: Observable<BrowseNewsIntent>) {
        disposables.add(intents.subscribe(intentsSubject::onNext))
    }

    override fun states(): Observable<BrowseNewsViewState> = statesObservable

    /**
     * Compose all components to create the stream logic
     */
    private fun compose(): Observable<BrowseNewsViewState> {
        return intentsSubject
                .compose<BrowseNewsIntent>(intentFilter)
                .map<BrowseNewsAction>(this::actionFromIntent)
                // Special case where we do not want to pass this event down the stream
                .filter { action -> action !is BrowseNewsAction.SkipMe }
                .compose(actionProcessorHolder.actionProcessor)
                // Cache each state and pass it to the reducer to create a new state from
                // the previous cached one and the latest Result emitted from the action processor.
                // The Scan operator is used here for the caching.
                .scan(BrowseNewsViewState.idle(), reducer)
                // When a reducer just emits previousState, there's no reason to call render. In fact,
                // redrawing the UI in cases like this can cause jank (e.g. messing up snackbar animations
                // by showing the same snackbar twice in rapid succession).
                .distinctUntilChanged()
                // Emit the last one event of the stream on subscription
                // Useful when a View rebinds to the ViewModel after rotation.
                .replay(1)
                // Create the stream on creation without waiting for anyone to subscribe
                // This allows the stream to stay alive even when the UI disconnects and
                // match the stream's lifecycle to the ViewModel's one.
                .autoConnect(0)
    }

    /**
     * Translate an [MviIntent] to an [MviAction].
     * Used to decouple the UI and the business logic to allow easy testings and reusability.
     */
    private fun actionFromIntent(intent: BrowseNewsIntent): BrowseNewsAction {
        return when (intent) {
            is BrowseNewsIntent.InitialIntent -> {
                PopulateNewsAction("eg","1")
            }
            is BrowseNewsIntent.LoadMoreIntent -> {
                PopulateNewsAction("eg",intent.page.toString())
            }
        }
    }

    override fun onCleared() {
        disposables.dispose()
    }

    companion object {
        /**
         * The Reducer is where [MviViewState], that the [MviView] will use to
         * render itself, are created.
         * It takes the last cached [MviViewState], the latest [MviResult] and
         * creates a new [MviViewState] by only updating the related fields.
         * This is basically like a big switch statement of all possible types for the [MviResult]
         */
        private val reducer = BiFunction { previousViewState: BrowseNewsViewState, result: BrowseNewsResult ->
            when (result) {
                is BrowseNewsResult.PopulateTaskResult -> when (result) {
                    is BrowseNewsResult.PopulateTaskResult.Success -> {
                        result.news.let { news ->
                            if (news.status == "ok") {
                                previousViewState.copy(isLoaded = true, error = null, newsList = news.articles)
                            } else {
                                previousViewState
                            }
                        }
                    }
                    is BrowseNewsResult.PopulateTaskResult.Failure -> previousViewState.copy(error = result.error)
                    is BrowseNewsResult.PopulateTaskResult.InFlight -> previousViewState
                }
            }
        }
    }
}
