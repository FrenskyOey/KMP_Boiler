package com.interview.prep.kmp_learn

import core.di.coreConfigModule
import core.di.coreDatabaseModule
import core.di.coreNetworkModule
import core.di.corePreferencesModule
import di.appModule
import feature.news.di.newsModule
import feature.settings.di.settingsModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            coreNetworkModule,
            coreDatabaseModule,
            corePreferencesModule,
            coreConfigModule,
            newsModule,
            settingsModule,
            appModule
        )
    }
}
