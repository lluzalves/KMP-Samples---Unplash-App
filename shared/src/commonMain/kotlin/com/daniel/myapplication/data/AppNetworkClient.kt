package com.daniel.myapplication.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AppNetworkClient(val engine: HttpClientEngine = HttpClient().engine) {
    private val baseUrl =  "api.unsplash.com"

    private val httpClient = HttpClient(engine) {

        defaultRequest {
            host = baseUrl
            url {
                protocol = URLProtocol.HTTPS
            }
        }

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = false
                isLenient = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10000L
            socketTimeoutMillis = 15000L
            connectTimeoutMillis = 20000L
        }

        HttpResponseValidator {
            validateResponse {
                if (it.status.isSuccess().not()) {
                    throw AppNetworkClientException("HTTP ERROR - Code: ${it.status}, Body: ${it.bodyAsText()}")
                }
            }
        }
    }

    fun appHttpClient(): HttpClient = httpClient
}

class AppNetworkClientException(message: String) : Exception(message)