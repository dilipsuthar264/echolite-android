package com.echolite.app.musicPlayer.debouncer

import android.util.Log
import javax.inject.Singleton

@Singleton
object Debouncer {
    private val lastFunctionCallTime = mutableMapOf<String, Long>()
    private const val DEBOUNCE_TIME = 200L // Constant since it’s fixed

    fun shouldIgnoreCall(functionName: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastCallTime = lastFunctionCallTime[functionName] ?: 0L
        if (currentTime - lastCallTime < DEBOUNCE_TIME) {
            Log.d("Debouncer", "Ignoring rapid $functionName() calls")
            return true
        }
        lastFunctionCallTime[functionName] = currentTime
        return false
    }

    fun clear(functionName: String) {
        lastFunctionCallTime.remove(functionName)
    }

    fun clearAll() {
        lastFunctionCallTime.clear()
    }
}