package com.example.techpowerhour.data.repository

import android.util.Log

/**
 * The class for all repositories to inherit from, so that the [onInit] and [onDestroy] methods are present.
 */
open class BaseRepository {
    /**
     * To be called when:
     * - The class is first initialised
     * - The class needs to be reset
     */
    open fun onInit() {
        Log.d(this.javaClass.toGenericString(), "Initialised")
    }

    /**
     * To be called when:
     * - The class needs to clean up cache and listeners
     */
    open fun onDestroy() {
        Log.d(this.javaClass.toGenericString(), "Destroyed")
    }
}