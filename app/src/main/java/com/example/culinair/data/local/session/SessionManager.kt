package com.example.culinair.data.local.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.culinair.domain.model.AuthMethod
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Created by John Ralph Dela Rosa on 7/26/2025.
 */
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val AUTH_METHOD = stringPreferencesKey("auth_method")
    }

    @Volatile
    private var cachedAccessToken: String? = null

    @Volatile
    private var cachedRefreshToken: String? = null

    @Volatile
    private var cachedUserId: String? = null

    @Volatile
    private var cachedAuthMethod: AuthMethod? = null

    suspend fun saveUserSession(accessToken: String, refreshToken: String, userId: String, authMethod: AuthMethod) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = accessToken
            prefs[REFRESH_TOKEN] = refreshToken
            prefs[USER_ID] = userId
            prefs[AUTH_METHOD] = when (authMethod) {
                is AuthMethod.EmailPassword -> "EMAIL_PASSWORD"
                is AuthMethod.Google -> "GOOGLE"
            }
        }
        cachedAccessToken = accessToken
        cachedRefreshToken = refreshToken
        cachedUserId = userId
        cachedAuthMethod = authMethod
    }

    suspend fun getAccessToken(): String? {
        if (cachedAccessToken == null) {
            cachedAccessToken = dataStore.data.first()[ACCESS_TOKEN]
        }
        return cachedAccessToken
    }

    suspend fun getUserId(): String? {
        if (cachedUserId == null) {
            cachedUserId = dataStore.data.first()[USER_ID]
        }
        return cachedUserId
    }

    suspend fun getRefreshToken(): String? {
        if (cachedRefreshToken == null) {
            cachedRefreshToken = dataStore.data.first()[REFRESH_TOKEN]
        }
        return cachedRefreshToken
    }

    suspend fun isLoggedIn(): Boolean {
        return getAccessToken() != null && getRefreshToken() != null
    }

    suspend fun getAuthMethod(): AuthMethod? {
        if (cachedAuthMethod == null) {
            val authMethodString = dataStore.data.first()[AUTH_METHOD]
            cachedAuthMethod = when (authMethodString) {
                "EMAIL_PASSWORD" -> AuthMethod.EmailPassword
                "GOOGLE" -> AuthMethod.Google
                else -> null // Handle invalid or missing values
            }
        }
        return cachedAuthMethod
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
            prefs.remove(USER_ID)
            prefs.remove(AUTH_METHOD)
        }
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedUserId = null
        cachedAuthMethod = null
    }
}