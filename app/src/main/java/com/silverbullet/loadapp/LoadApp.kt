package com.silverbullet.loadapp

import android.app.Application
import timber.log.Timber

class LoadApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}