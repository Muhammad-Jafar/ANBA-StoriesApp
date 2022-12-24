package jafar.stories.utils

import android.util.Patterns

object FormValidator {
    fun isNameValid(name: String): Boolean {
        return if (name.contains('@')) Patterns.EMAIL_ADDRESS.matcher(name).matches()
        else name.isNotBlank()
    }

    fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isPasswordValid(password: String): Boolean = password.length >= 6
}