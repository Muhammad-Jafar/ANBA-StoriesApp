package jafar.stories.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import jafar.stories.utils.Constanta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SharePreferences private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun clearUserToken() = dataStore.edit { it.clear() }
    suspend fun saveUserToken(token: String) = dataStore.edit { it[TOKEN_LOGIN] = token }
    fun getUserToken(): Flow<String?> =
        dataStore.data.map { it[TOKEN_LOGIN] ?: Constanta.defaultValue }

    suspend fun saveLoginState(isLogin: Boolean) = dataStore.edit { it[STATE_LOGIN] = isLogin }
    fun getLoginState() = dataStore.data.map { it[STATE_LOGIN] ?: false }

    companion object {
        @Volatile
        private var INSTANCE: SharePreferences? = null
        private val TOKEN_LOGIN = stringPreferencesKey(Constanta.Preferences.Token.name)
        private val STATE_LOGIN = booleanPreferencesKey(Constanta.Preferences.IsLogin.name)

        fun getInstance(dataStore: DataStore<Preferences>): SharePreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SharePreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
