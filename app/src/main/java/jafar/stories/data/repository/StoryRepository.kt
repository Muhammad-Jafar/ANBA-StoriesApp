package jafar.stories.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import jafar.stories.data.database.StoryDatabase
import jafar.stories.data.model.AddStoryRequest
import jafar.stories.data.model.ListStory
import jafar.stories.data.preferences.SharePreferences
import jafar.stories.data.remote.ApiService
import jafar.stories.data.remote.StoryRemoteMediator
import kotlinx.coroutines.flow.first

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val pref: SharePreferences,
) : BaseRepository() {

    fun getStories(): LiveData<PagingData<ListStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, pref),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStory() }
        ).liveData
    }

    fun getStoriesLocation() = liveData {
        emit(Result.Loading)
        try {
            val token = pref.getUserToken().first()
            val response = apiService.getAllStories("Bearer $token", 1, 10, 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("$e"))
        }
    }

    fun addStory(request: AddStoryRequest) = liveData {
        emit(Result.Loading)
        try {
            val token = pref.getUserToken().first()
            val response =
                apiService.uploadStory("Bearer $token", request.imageMultipart, request.description)
            if (!response.error) emit(Result.Success(response))
            else emit(Result.Error(response.message))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun removeUserToken() = pref.clearUserToken()
    suspend fun changeLoginState(isLogin: Boolean) = pref.saveLoginState(isLogin)
}