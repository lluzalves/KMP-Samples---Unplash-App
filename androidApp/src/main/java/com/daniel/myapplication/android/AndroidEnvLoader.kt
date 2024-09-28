package com.daniel.myapplication.android

import android.content.Context
import org.koin.core.annotation.Single
import java.io.InputStreamReader
import java.util.Properties


@Single
class AndroidEnvLoader(private val context: Context) {
    fun loadEnvFile(): Map<String, String> {
        val inputStream = context.assets.open(".env")
        val properties = Properties().apply {
            load(InputStreamReader(inputStream))
        }

        return properties.entries.associate { it.key.toString() to it.value.toString() }
    }
}