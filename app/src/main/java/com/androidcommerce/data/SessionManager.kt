package com.androidcommerce.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {
    private val userIdKey = longPreferencesKey("user_id")

    val currentUserId: Flow<Long?> = context.sessionDataStore.data.map { prefs ->
        prefs[userIdKey]
    }

    suspend fun setUserId(userId: Long?) {
        context.sessionDataStore.edit { prefs ->
            if (userId == null) {
                prefs.remove(userIdKey)
            } else {
                prefs[userIdKey] = userId
            }
        }
    }
}
