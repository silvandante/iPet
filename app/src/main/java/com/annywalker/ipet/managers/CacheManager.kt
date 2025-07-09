package com.annywalker.ipet.managers

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun setBoolean(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { it[key] = value }
    }

    suspend fun setString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { it[key] = value }
    }

    suspend fun setInt(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { it[key] = value }
    }

    suspend fun setFloat(key: Preferences.Key<Float>, value: Float) {
        dataStore.edit { it[key] = value }
    }

    suspend fun setLong(key: Preferences.Key<Long>, value: Long) {
        dataStore.edit { it[key] = value }
    }

    suspend fun getBoolean(key: Preferences.Key<Boolean>, default: Boolean = false): Boolean {
        return dataStore.data.first()[key] ?: default
    }

    suspend fun getString(key: Preferences.Key<String>, default: String = ""): String {
        return dataStore.data.first()[key] ?: default
    }

    suspend fun getInt(key: Preferences.Key<Int>, default: Int = 0): Int {
        return dataStore.data.first()[key] ?: default
    }

    suspend fun getFloat(key: Preferences.Key<Float>, default: Float = 0f): Float {
        return dataStore.data.first()[key] ?: default
    }

    suspend fun getLong(key: Preferences.Key<Long>, default: Long = 0L): Long {
        return dataStore.data.first()[key] ?: default
    }

    fun observeBoolean(key: Preferences.Key<Boolean>, default: Boolean = false): Flow<Boolean> =
        dataStore.data.map { it[key] ?: default }

    fun observeString(key: Preferences.Key<String>, default: String = ""): Flow<String> =
        dataStore.data.map { it[key] ?: default }

    fun observeInt(key: Preferences.Key<Int>, default: Int = 0): Flow<Int> =
        dataStore.data.map { it[key] ?: default }

    fun observeFloat(key: Preferences.Key<Float>, default: Float = 0f): Flow<Float> =
        dataStore.data.map { it[key] ?: default }

    fun observeLong(key: Preferences.Key<Long>, default: Long = 0L): Flow<Long> =
        dataStore.data.map { it[key] ?: default }
}

