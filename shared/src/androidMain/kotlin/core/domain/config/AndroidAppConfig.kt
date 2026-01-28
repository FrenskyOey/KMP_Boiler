package core.domain.config

import com.interview.prep.kmp_learn.shared.BuildConfig

class AndroidAppConfig : AppConfig {
    override val baseApiUrl: String = BuildConfig.BASE_API_URL
    override val flavorName: String = BuildConfig.FLAVOR_NAME
}

actual fun createAppConfig(): AppConfig = AndroidAppConfig()
