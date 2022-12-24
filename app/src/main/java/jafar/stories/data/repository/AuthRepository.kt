package jafar.stories.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import jafar.stories.data.model.LoginRequest
import jafar.stories.data.model.RegisterRequest
import jafar.stories.data.preferences.SharePreferences
import jafar.stories.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val pref: SharePreferences
) : BaseRepository() {

    fun doRegister(request: RegisterRequest) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(request.name, request.email, request.password)
            if (!response.error) emit(Result.Success(response))
            else emit(Result.Error(response.message))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun doLogin(request: LoginRequest) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(request.email, request.password)
            emit(Result.Success(response.loginResult))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun saveUserToken(token: String) = pref.saveUserToken(token)
    suspend fun saveLoginState(isLogin: Boolean) = pref.saveLoginState(isLogin)
    fun getLoginState() = pref.getLoginState()

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null
        fun getInstance(apiService: ApiService, pref: SharePreferences): AuthRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(apiService, pref)
            }.also { INSTANCE = it }
    }
}
