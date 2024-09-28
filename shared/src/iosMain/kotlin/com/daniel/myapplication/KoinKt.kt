package com.daniel.myapplication

import org.koin.core.context.startKoin
import org.koin.core.context.startKoin


fun doInitKoinIOS(){
    startKoin {
        modules()
    }
}