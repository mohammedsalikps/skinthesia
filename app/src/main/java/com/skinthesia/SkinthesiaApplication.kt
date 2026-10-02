package com.skinthesia

import android.app.Application
import com.skinthesia.data.SkinthesiaRepository

/** Wires the app-wide [SkinthesiaRepository] to local persistence once, before any screen needs it. */
class SkinthesiaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SkinthesiaRepository.initialize(this)
    }
}
