package jafar.stories.data.remote

import jafar.stories.data.model.AddStoryResponse
import jafar.stories.data.model.DataLoginResponse
import jafar.stories.data.model.DataRegisterResponse
import jafar.stories.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): DataRegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): DataLoginResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") Bearer: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): AddStoryResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") Bearer: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int? = null ?: 0,
    ): StoryResponse
}
