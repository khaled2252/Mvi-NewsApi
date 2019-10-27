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

package com.khaled.newsmvi.ui.browsenews.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.khaled.newsmvi.R
import com.khaled.newsmvi.data.models.Article
import com.khaled.newsmvi.di.helper.Injectable
import com.khaled.newsmvi.mvibase.MviIntent
import com.khaled.newsmvi.mvibase.MviView
import com.khaled.newsmvi.mvibase.MviViewModel
import com.khaled.newsmvi.mvibase.MviViewState
import com.khaled.newsmvi.ui.browsenews.mvi.BrowseNewsIntent
import com.khaled.newsmvi.ui.browsenews.mvi.BrowseNewsViewState
import com.khaled.newsmvi.ui.browsenews.viewmodel.BrowseNewsViewModel
import com.khaled.newsmvi.util.EndlessRecyclerViewScrollListener
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_news.*
import javax.inject.Inject


/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */

class BrowseNewsFragment : Fragment(), MviView<BrowseNewsIntent, BrowseNewsViewState>, Injectable {
    @Inject
    lateinit var viewModel: BrowseNewsViewModel

    private val disposables = CompositeDisposable()
    private val loadMoreIntentPublisher = PublishSubject.create<BrowseNewsIntent.LoadMoreIntent>()

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
                .also {
                    setHasOptionsMenu(true)
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        val mRecyclerView = this.recyclerViewNews
        mRecyclerView.apply {
            val layoutManager = LinearLayoutManager(this@BrowseNewsFragment.context)
            this.layoutManager = layoutManager
            adapter = NewsListAdapter()
            val scrollListener = object : EndlessRecyclerViewScrollListener(layoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    loadMoreIntentPublisher.onNext(BrowseNewsIntent.LoadMoreIntent(page))
                }
            }
            addOnScrollListener(scrollListener)
        }
    }

    /**
     * Connect the [MviView] with the [MviViewModel]
     * We subscribe to the [MviViewModel] before passing it the [MviView]'s [MviIntent]s.
     * If we were to pass [MviIntent]s to the [MviViewModel] before listening to it,
     * emitted [MviViewState]s could be lost
     */
    private fun bind() {
        // Subscribe to the ViewModel and call render for every emitted state
        disposables.add(viewModel.states().subscribe(this::render))
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())
    }

    override fun intents(): Observable<BrowseNewsIntent> {
        return Observable.merge(initialIntent(),loadMoreIntent())
    }

    /**
     * The initial Intent the [MviView] emit to convey to the [MviViewModel]
     * that it is ready to receive data.
     * This initial Intent is also used to pass any parameters the [MviViewModel] might need
     * to render the initial [MviViewState] (e.g. the task id to load).
     */
    private fun initialIntent(): Observable<BrowseNewsIntent> {
        return Observable.just(BrowseNewsIntent.InitialIntent)
    }

    private fun loadMoreIntent(): Observable<BrowseNewsIntent.LoadMoreIntent> {
        return loadMoreIntentPublisher
    }

    override fun render(state: BrowseNewsViewState) {
        if (state.isLoaded)
            showArticlesList(state.newsList)
        else
            showEmptyListError()
    }

    private fun showEmptyListError() {
        //TODO
    }

    private fun showArticlesList(articlesList: ArrayList<Article>) {
        val adapter = (this.recyclerViewNews.adapter as NewsListAdapter)
        adapter.insertList(articlesList)
    }

    companion object {
        operator fun invoke(): BrowseNewsFragment = BrowseNewsFragment()
    }
}
