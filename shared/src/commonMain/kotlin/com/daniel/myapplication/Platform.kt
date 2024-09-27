package com.daniel.myapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform