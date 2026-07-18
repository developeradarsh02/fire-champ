package com.firechamp.tournament.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "fire_champ_session")

/**
 * Session Manager - DataStore-based session persistence.
 *
 * Login ke baad user data save hota hai, app restart hone par auto-login hota hai.
 * Logout par data clear ho jata hai.
 *
 * Task 11 - Modern replacement for SharedPreferences.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        // Notification preferences (Task: persistent toggle fix)
        private val KEY_PUSH_ENABLED = booleanPreferencesKey("push_enabled")
        private val KEY_MATCH_REMINDERS = booleanPreferencesKey("match_reminders")
        private val KEY_RESULT_ALERTS = booleanPreferencesKey("result_alerts")
        private val KEY_PROMO_ALERTS = booleanPreferencesKey("promo_alerts")
    }

    /** Logged in user data (null if not logged in). */
    val sessionFlow: Flow<SessionData?> = dataStore.data.map { prefs ->
        if (prefs[KEY_IS_LOGGED_IN] == true) {
            SessionData(
                userId = prefs[KEY_USER_ID] ?: "",
                username = prefs[KEY_USERNAME] ?: "",
                email = prefs[KEY_EMAIL] ?: ""
            )
        } else null
    }

    // ============ NOTIFICATION PREFERENCES ============
    // Default values: push=true, reminders=true, results=true, promos=false
    val pushEnabledFlow: Flow<Boolean> = dataStore.data.map { it[KEY_PUSH_ENABLED] ?: true }
    val matchRemindersFlow: Flow<Boolean> = dataStore.data.map { it[KEY_MATCH_REMINDERS] ?: true }
    val resultAlertsFlow: Flow<Boolean> = dataStore.data.map { it[KEY_RESULT_ALERTS] ?: true }
    val promoAlertsFlow: Flow<Boolean> = dataStore.data.map { it[KEY_PROMO_ALERTS] ?: false }

    suspend fun setPushEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_PUSH_ENABLED] = enabled }
    }

    suspend fun setMatchReminders(enabled: Boolean) {
        dataStore.edit { it[KEY_MATCH_REMINDERS] = enabled }
    }

    suspend fun setResultAlerts(enabled: Boolean) {
        dataStore.edit { it[KEY_RESULT_ALERTS] = enabled }
    }

    suspend fun setPromoAlerts(enabled: Boolean) {
        dataStore.edit { it[KEY_PROMO_ALERTS] = enabled }
    }

    suspend fun saveSession(userId: String, username: String, email: String) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
            prefs[KEY_EMAIL] = email
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_USER_ID)
            prefs.remove(KEY_USERNAME)
            prefs.remove(KEY_EMAIL)
            prefs[KEY_IS_LOGGED_IN] = false
            // Note: Notification preferences are NOT cleared on logout
            // (user wants same settings after re-login)
        }
    }
}

data class SessionData(
    val userId: String,
    val username: String,
    val email: String
)