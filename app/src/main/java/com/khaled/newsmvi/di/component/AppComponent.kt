package com.khaled.newsmvi.di.component

import android.app.Application
import com.khaled.newsmvi.AppInstance
import com.khaled.newsmvi.di.module.ActivityModule
import com.khaled.newsmvi.di.module.AppModule
import com.khaled.newsmvi.di.module.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityModule::class, NetworkModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun baseUrl(url: String): Builder

        fun build(): AppComponent
    }

    fun inject(appInstance: AppInstance)
}