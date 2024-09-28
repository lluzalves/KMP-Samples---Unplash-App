package com.daniel.myapplication.android.application

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KmpApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KmpApp)
        }
    }
}