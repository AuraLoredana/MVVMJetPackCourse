package com.example.mvvmjetpackcourse.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit

class SharedPreferencesHelper {

    companion object {
        private var prefs: SharedPreferences? = null
        private const val PREF_TIME = "Pref time"
        @Volatile private var instance: SharedPreferencesHelper? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): SharedPreferencesHelper = instance ?: synchronized(LOCK) {
            instance ?: buildHelper(context).also {
                instance = it
            }
        }

        private fun buildHelper(context: Context): SharedPreferencesHelper {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesHelper()
        }
    }

    fun saveUpdateTIme(time: Long) {
        prefs?.edit(commit = true) { putLong(PREF_TIME, time) }
    }

    fun getUpdateTIme() = prefs?.getLong(PREF_TIME, 0)

    fun getCacheDuration() =prefs?.getString("pref_cache_duration", "")
}