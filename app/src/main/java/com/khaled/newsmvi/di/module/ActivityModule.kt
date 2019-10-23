package com.khaled.newsmvi.di.module

import com.khaled.newsmvi.ui.browsenews.view.BrowseNewsActivity
import com.khaled.newsmvi.di.builder.MainActivityFragmentBuilderModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [MainActivityFragmentBuilderModule::class])
    abstract fun contributeMainActivity(): BrowseNewsActivity

}