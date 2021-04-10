package com.example.techpowerhour.data.repository

import android.util.Log

open class BaseRepository {
    open fun onInit() {
        Log.d(this.javaClass.toGenericString(), "Initialised")
    }

    open fun onDestroy() {
        Log.d(this.javaClass.toGenericString(), "Destroyed")
    }
}