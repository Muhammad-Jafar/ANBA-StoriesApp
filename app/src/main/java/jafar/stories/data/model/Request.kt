package jafar.stories.data.model

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class RegisterRequest(val name: String, val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)

data class AddStoryRequest(
    val imageMultipart: MultipartBody.Part,
    val description: RequestBody,
    val lat: Double? = null,
    val lon: Double? = null
)