package jafar.stories.features.auth.utils

import jafar.stories.data.model.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object Dummy {

    /* REQUEST */
    fun generateDummyLoginRequest(): LoginRequest {
        return LoginRequest("user@testing.com", "user123")
    }

    fun generateDummyRegisterRequest(): RegisterRequest {
        return RegisterRequest("user testing", "user@testing.com", "user123")
    }

    fun generateDummyAddStoryRequest(): AddStoryRequest {
        val file = File("/storage/emulated/0/DCIM/Facebook/FB_IMG_1496146656900.jpg")
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part =
            MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
        val descriptionRequestBody = "description testing".toRequestBody("text/plain".toMediaType())

        return AddStoryRequest(imageMultipart, descriptionRequestBody)
    }

    /* RESPONSE */
    fun generateDummyLoginResponse(): LoginResult {
        return LoginResult("user-testing", "user-001", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
    }

    fun generateDummyRegisterResponse(): DataRegisterResponse {
        return DataRegisterResponse(false, "success")
    }

    fun generateDummyAddStoryResponse(): AddStoryResponse {
        return AddStoryResponse(false, "success")
    }

    /* DATA STORY */
    fun generateDummyStoriesEntity(): StoryResponse {
        val list: MutableList<ListStory> = arrayListOf()
        for (i in 0..10) {
            val stories = ListStory(
                "1",
                "User testing",
                "Description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-10-03T03:48:09.330Z",
                "-6.288288288288288",
                "106.82289451908555",
            )
            list.add(stories)
        }
        return StoryResponse(false, "Stories fetched successfully", list)
    }

    fun generateDummyStories(): List<ListStory> {
        val items: MutableList<ListStory> = arrayListOf()
        for (i in 0..100) {
            val story = ListStory(
                "story-$i",
                "dicoding",
                "dicoding story desc",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-10-03T03:48:09.330Z",
                "-6.288288288288288",
                "106.82289451908555",
            )
            items.add(story)
        }
        return items
    }
}