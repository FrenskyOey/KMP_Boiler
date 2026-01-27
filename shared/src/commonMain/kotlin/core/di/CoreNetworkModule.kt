package core.di

import core.data.remote.util.JsonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module

val coreNetworkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(JsonSerializer.json)
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }
}
