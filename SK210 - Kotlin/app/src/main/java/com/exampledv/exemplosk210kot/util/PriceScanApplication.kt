package com.exampledv.exemplosk210kot.util

import android.app.Application
import android.content.Context

class PriceScanApplication : Application() {

    companion object {
        private lateinit var instance: PriceScanApplication

        fun getContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
