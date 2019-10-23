package com.khaled.newsmvi.di.builder

import com.khaled.newsmvi.ui.browsenews.view.BrowseNewsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityFragmentBuilderModule {
    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): BrowseNewsFragment
}