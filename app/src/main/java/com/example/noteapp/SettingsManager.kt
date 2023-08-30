package com.example.noteapp

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import kotlinx.coroutines.flow.first

object SettingsManager {

    lateinit var dataStore: DataStore<Preferences>

    const val COLUMN_VIEW = "column_view"
    const val MAX_LINE = "max_line"
    const val TEXT_SIZE = "text_size"
    const val DARK_MODE = "dark_mode"

    suspend fun save(key:String, value: String) {
        val dataStoreKey = preferencesKey<String>(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    suspend fun read(key:String) : String? {
        val dataStoreKey = preferencesKey<String>(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }
}