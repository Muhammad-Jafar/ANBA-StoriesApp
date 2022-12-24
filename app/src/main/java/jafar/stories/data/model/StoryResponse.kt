package jafar.stories.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/* Load Data Response */
data class StoryResponse(
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String,
    @field:SerializedName("listStory") val listStory: List<ListStory> = arrayListOf()
)

/* Database */
@Entity(tableName = "story")
@Parcelize
data class ListStory(
    @PrimaryKey
    @field:SerializedName("id") val id: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("description") val description: String,
    @field:SerializedName("photoUrl") val photoUrl: String,
    @field:SerializedName("createdAt") val createdAt: String,
    @field:SerializedName("lon") val lon: String? = null,
    @field:SerializedName("lat") val lat: String? = null,
) : Parcelable

@Entity(tableName = "story_remote_keys")
data class StoryRemoteKeys(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
)

/* Add Story Response */
data class AddStoryResponse(
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String
)

@Parcelize
data class AddStoryLocation(
    val isLocationPicked: Boolean? = false,
    val lat: Double? = 0.0,
    val lon: Double? = 0.0,
): Parcelable

/* Register Response */
data class DataRegisterResponse(
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String
)

data class RegisterFormState(
    val nameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)

/* Login Response */
data class DataLoginResponse(
    @field:SerializedName("loginResult") val loginResult: LoginResult,
    @field:SerializedName("error") val error: Boolean,
    @field:SerializedName("message") val message: String
)

data class LoginResult(
    @field:SerializedName("name") val name: String,
    @field:SerializedName("userId") val userId: String,
    @field:SerializedName("token") val token: String
)

data class LoginFormState(
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
