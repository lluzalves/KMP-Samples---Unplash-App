package com.daniel.myapplication

class EnvProvider {
    private val env: Map<String, String> = loadEnvFile()

    val accessKey: String
        get() = env["UNSPLASH_ACCESS_KEY"] ?: throw IllegalStateException("Access key not found")

    val secretKey: String
        get() = env["UNSPLASH_SECRET_KEY"] ?: throw IllegalStateException("Secret key not found")
}