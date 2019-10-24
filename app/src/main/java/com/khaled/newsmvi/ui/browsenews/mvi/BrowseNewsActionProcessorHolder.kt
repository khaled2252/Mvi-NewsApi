package com.khaled.newsmvi.ui.browsenews.mvi

import com.khaled.newsmvi.ui.browsenews.repository.NewsRepository
import com.khaled.newsmvi.mvibase.MviAction
import com.khaled.newsmvi.mvibase.MviResult
import com.khaled.newsmvi.mvibase.MviViewModel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * Contains and executes the business logic for all emitted [MviAction]
 * and returns one unique [Observable] of [MviResult].
 *
 *
 * This could have been included inside the [MviViewModel]
 * but was separated to ease maintenance, as the [MviViewModel] was getting too big.
 */
class BrowseNewsActionProcessorHolder @Inject constructor(
        private val newsRepository: NewsRepository
) {
    private val populateTaskProcessor =
            ObservableTransformer<BrowseNewsAction.PopulateNewsAction, BrowseNewsResult.PopulateTaskResult> { actions ->
                actions.flatMap { action ->
                    newsRepository.getNewsResult(action.country)
                            // Transform the Single to an Observable to allow emission of multiple
                            // events down the stream (e.g. the InFlight event)
                            .toObservable()
                            // Wrap returned data into an immutable object
                            .map { newsObject -> BrowseNewsResult.PopulateTaskResult.Success(news = newsObject) }
                            .cast(BrowseNewsResult.PopulateTaskResult::class.java)
                            // Wrap any error into an immutable object and pass it down the stream
                            // without crashing.
                            // Because errors are data and hence, should just be part of the stream.
                            .onErrorReturn(BrowseNewsResult.PopulateTaskResult::Failure)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                            // doing work and waiting on a response.
                            // We emit it after observing on the UI thread to allow the event to be emitted
                            // on the current frame and avoid jank.
                            .startWith(BrowseNewsResult.PopulateTaskResult.InFlight)
                }
            }


    /**
     * Splits the [Observable] to match each type of [MviAction] to
     * its corresponding business logic processor. Each processor takes a defined [MviAction],
     * returns a defined [MviResult]
     * The global actionProcessor then merges all [Observable] back to
     * one unique [Observable].
     *
     *
     * The splitting is done using [Observable.publish] which allows almost anything
     * on the passed [Observable] as long as one and only one [Observable] is returned.
     *
     *
     * An security layer is also added for unhandled [MviAction] to allow early crash
     * at runtime to easy the maintenance.
     */
    internal var actionProcessor =
            ObservableTransformer<BrowseNewsAction, BrowseNewsResult> { actions ->
                actions.publish { shared ->
//                    Observable.merge<BrowseNewsResult>(
                            // Match PopulateTasks to populateTaskProcessor
                            shared.ofType(BrowseNewsAction.PopulateNewsAction::class.java)
                                    .compose(populateTaskProcessor).cast(BrowseNewsResult::class.java)
                            .mergeWith(
                                    // Error for not implemented actions
                                    shared.filter { v ->
                                        v !is BrowseNewsAction.PopulateNewsAction
                                                    }
                                            .flatMap { w ->
                                                Observable.error<BrowseNewsResult>(
                                                        IllegalArgumentException("Unknown Action type: $w"))
                                            })
                }
            }
}
