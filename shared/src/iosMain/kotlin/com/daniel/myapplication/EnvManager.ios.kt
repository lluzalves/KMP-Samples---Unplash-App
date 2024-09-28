package com.daniel.myapplication

import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.stringWithContentsOfFile

actual fun loadEnvFile(): Map<String, String> {
    val path = NSBundle.mainBundle.pathForResource(".env", ofType = null) ?: throw IllegalStateException(".env file not found")

    val contents = NSString.stringWithContentsOfFile(path).toString() ?: throw IllegalStateException("Failed to load .env file")
    val envVariables = contents.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

    return envVariables.associate {
        val (key, value) = it.split("=")
        key.trim() to value.trim()
    }
}