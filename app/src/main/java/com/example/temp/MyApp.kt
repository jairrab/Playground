package com.example.temp

import android.app.Application
import com.github.jairrab.androidutilities.timberutil.DebugTreeMod
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTreeMod("^^"))
        }
    }
}