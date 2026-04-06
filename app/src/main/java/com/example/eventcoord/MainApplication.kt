package com.example.eventcoord


import android.app.Application
import com.cloudinary.android.MediaManager

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // credenciales cloudinary
        val config = mapOf(
            "cloud_name" to "duzpc08q3",
            "api_key" to "528144598885136",
            "api_secret" to "8RbSxC72dcHzZPVFow7Iu0qyhFc"
        )

        MediaManager.init(this, config)
    }
}