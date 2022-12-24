package jafar.stories.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import jafar.stories.data.database.StoryDatabase
import jafar.stories.data.preferences.SharePreferences
import jafar.stories.data.remote.ApiConfig
import jafar.stories.data.repository.AuthRepository
import jafar.stories.data.repository.StoryRepository

private val Context.dataStore by preferencesDataStore(name = "share_pref")

object Injection {
    fun provideAuth(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val pref = SharePreferences.getInstance(context.dataStore)
        return AuthRepository.getInstance(apiService, pref)
    }

    fun provideStory(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        val pref = SharePreferences.getInstance(context.dataStore)
        return StoryRepository(database, apiService, pref)
    }
}
