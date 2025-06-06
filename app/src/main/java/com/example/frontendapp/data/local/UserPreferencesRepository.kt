package com.example.frontendapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.frontendapp.data.model.UI.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreferencesRepository(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

    private fun accessLocationKey(userId: String) = booleanPreferencesKey("access_location_$userId")

    // Retorna las preferencias del usuario dado
    fun getPreferences(userId: String): Flow<UserPreferences> =
        context.dataStore.data
            .map { preferences ->
                UserPreferences(
                    accessLocation = preferences[accessLocationKey(userId)] ?: false
                )
            }

    // Actualiza la preferencia de acceso a ubicación para el usuario dado
    suspend fun updateAccessLocation(userId: String, access: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[accessLocationKey(userId)] = access
        }
    }
}

