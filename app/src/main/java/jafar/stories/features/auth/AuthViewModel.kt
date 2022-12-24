package jafar.stories.features.auth

import androidx.lifecycle.*
import jafar.stories.R
import jafar.stories.data.model.LoginFormState
import jafar.stories.data.model.LoginRequest
import jafar.stories.data.model.RegisterFormState
import jafar.stories.data.model.RegisterRequest
import jafar.stories.data.repository.AuthRepository
import jafar.stories.utils.FormValidator
import kotlinx.coroutines.launch

open class AuthViewModel : ViewModel()

class SplashScreenViewModel(repo: AuthRepository) : AuthViewModel() {
    val checkLoginState = repo.getLoginState().asLiveData()
}

class RegisterViewModel(private val repo: AuthRepository) : AuthViewModel() {
    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    fun registerDataChanged(name: String, email: String, password: String) {
        if (!FormValidator.isNameValid(name)) _registerForm.value =
            RegisterFormState(nameError = R.string.invalid_name)
        else if (!FormValidator.isEmailValid(email)) _registerForm.value =
            RegisterFormState(emailError = R.string.invalid_email)
        else if (!FormValidator.isPasswordValid(password)) _registerForm.value =
            RegisterFormState(passwordError = R.string.invalid_password)
        else _registerForm.value = RegisterFormState(isDataValid = true)
    }

    fun doRegister(request: RegisterRequest) = repo.doRegister(request)
}

open class SettingsViewModel(private val repo: AuthRepository) : AuthViewModel() {
    fun saveUserToken(token: String) = viewModelScope.launch { repo.saveUserToken(token) }
}

class LoginViewModel(private val repo: AuthRepository) : SettingsViewModel(repo) {
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    fun loginDataChanged(email: String, password: String) {
        if (!FormValidator.isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!FormValidator.isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun doLogin(request: LoginRequest) = repo.doLogin(request)
    fun saveLoginState(isLogin: Boolean) = viewModelScope.launch { repo.saveLoginState(isLogin) }
}