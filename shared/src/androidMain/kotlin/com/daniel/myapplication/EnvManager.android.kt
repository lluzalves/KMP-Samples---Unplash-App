package com.daniel.myapplication

import android.content.Context
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.InputStreamReader
import java.util.Properties

@Single
class AndroidEnvLoader : KoinComponent {
    private val context: Context by inject()
    fun load(): Map<String, String> {
        val inputStream = context.assets.open(".env")
        val properties = Properties().apply {
            load(InputStreamReader(inputStream))
        }
        return properties.entries.associate { it.key.toString() to it.value.toString() }
    }
}
actual fun loadEnvFile(): Map<String, String> {
    return AndroidEnvLoader().load()
}