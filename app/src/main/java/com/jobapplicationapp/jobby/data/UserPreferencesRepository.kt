package com.jobapplicationapp.jobby.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences

/**
 * Interface for user preferences
 */
interface UserPreferencesRepository {
    val isSyncEnabled: Flow<Boolean>
    suspend fun setIsSyncEnabled(isSyncEnabled: Boolean)
}

/**
 * Implementation of UserPreferencesRepository using DataStore
 */
class DataStoreUserPreferencesRepository (
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    //defining the keys
    private object PreferencesKeys {
        val IS_SYNC_ENABLED = booleanPreferencesKey("is_sync_enabled")
    }

    //reading the values
    //Flow to tell the current setting. and .map to extract the boolean we use
    override val isSyncEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_SYNC_ENABLED] ?: false // Default to false
        }

    //save value
    override suspend fun setIsSyncEnabled(isSyncEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_SYNC_ENABLED] = isSyncEnabled
        }
    }
}
