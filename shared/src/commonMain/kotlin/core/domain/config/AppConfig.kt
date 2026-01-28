package core.domain.config

interface AppConfig {
    val baseApiUrl: String
    val flavorName: String
}

expect fun createAppConfig(): AppConfig
