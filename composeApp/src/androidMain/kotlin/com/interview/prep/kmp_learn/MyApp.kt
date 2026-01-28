package com.interview.prep.kmp_learn

import android.app.Application
import core.di.coreDatabaseModule
import core.di.coreNetworkModule
import core.di.corePreferencesModule
import core.di.coreConfigModule
import di.appModule
import feature.news.di.newsModule
import feature.settings.di.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(
                // Core modules
                coreNetworkModule,
                coreDatabaseModule,
                corePreferencesModule,
                coreConfigModule,
                
                // Feature modules
                newsModule,
                settingsModule,
                
                // App module
                appModule
            )
        }
    }
}
