package com.interview.prep.kmp_learn.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppRoute {

    @Serializable
    data object Login : AppRoute()

    @Serializable
    data object Dashboard : AppRoute()

    @Serializable
    data class NewsDetail(val articleId: Long, val articleTitle: String) : AppRoute()

    @Serializable
    data object SettingsColor : AppRoute()

    @Serializable
    data object SettingsText : AppRoute()

    @Serializable
    data object SettingsButton : AppRoute()

    @Serializable
    data object SettingsForm : AppRoute()

    @Serializable
    data object SettingsNavbar : AppRoute()
}
