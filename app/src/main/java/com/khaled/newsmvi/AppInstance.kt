package com.khaled.newsmvi
import android.app.Activity
import android.app.Application
import com.khaled.newsmvi.di.helper.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class AppInstance : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun supportFragmentInjector(): DispatchingAndroidInjector<androidx.fragment.app.Fragment> =
            fragmentInjector

}

//ref check this https://github.com/matteopasotti/ComicsCatalog/tree/master/app/src/main/java/com/pasotti/matteo/wikiheroes/room