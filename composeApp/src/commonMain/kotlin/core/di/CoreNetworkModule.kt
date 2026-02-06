package core.di

import core.data.remote.util.JsonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

import com.russhwolf.settings.Settings
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import org.koin.core.qualifier.named

val coreNetworkModule = module {
    single {
        val secureSettings: Settings = get(named("secure"))
        HttpClient {
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = secureSettings.getStringOrNull("user_token")
                        if (token != null) {
                            BearerTokens(token, "")
                        } else {
                            null
                        }
                    }
                }
            }
            install(ContentNegotiation) {
                json(JsonSerializer.json)
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
}
