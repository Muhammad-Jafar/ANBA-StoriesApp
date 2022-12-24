package jafar.stories.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jafar.stories.data.di.Injection
import jafar.stories.data.repository.AuthRepository
import jafar.stories.data.repository.BaseRepository
import jafar.stories.data.repository.StoryRepository
import jafar.stories.features.auth.LoginViewModel
import jafar.stories.features.auth.RegisterViewModel
import jafar.stories.features.auth.SplashScreenViewModel
import jafar.stories.features.main.AddStoryViewModel
import jafar.stories.features.main.HomeViewModel
import jafar.stories.features.main.MapViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(private val repo: BaseRepository?) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) ->
                SplashScreenViewModel(repo as AuthRepository) as T
            (modelClass.isAssignableFrom(RegisterViewModel::class.java)) -> RegisterViewModel(repo as AuthRepository) as T
            (modelClass.isAssignableFrom(LoginViewModel::class.java)) -> LoginViewModel(repo as AuthRepository) as T

            (modelClass.isAssignableFrom(HomeViewModel::class.java)) -> HomeViewModel(repo as StoryRepository) as T
            (modelClass.isAssignableFrom(MapViewModel::class.java)) -> MapViewModel(repo as StoryRepository) as T
            (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) -> AddStoryViewModel(repo as StoryRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class" + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getAuthInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null || INSTANCE!!.repo !is AuthRepository) {
                val repository = Injection.provideAuth(context)
                INSTANCE = ViewModelFactory(repository)
            }
            return INSTANCE as ViewModelFactory
        }

        fun getStoryInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null || INSTANCE!!.repo !is StoryRepository) {
                val repository = Injection.provideStory(context)
                INSTANCE = ViewModelFactory(repository)
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
