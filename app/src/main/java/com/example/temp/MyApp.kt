package com.example.temp

import android.app.Application
import com.github.jairrab.androidutilities.timberutil.DebugTreeMod
import timber.log.Timber

class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTreeMod("^^"))
        }
    }
}