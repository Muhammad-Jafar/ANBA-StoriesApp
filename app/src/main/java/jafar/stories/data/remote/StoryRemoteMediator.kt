package jafar.stories.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import jafar.stories.data.database.StoryDatabase
import jafar.stories.data.model.ListStory
import jafar.stories.data.model.StoryRemoteKeys
import jafar.stories.data.preferences.SharePreferences
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val pref: SharePreferences
) : RemoteMediator<Int, ListStory>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ListStory>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> INITIAL_PAGE_INDEX
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val token = pref.getUserToken().first()
            val response =
                apiService.getAllStories("Bearer $token", page, state.config.pageSize).listStory
            val endOfPaginationReached = response.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyRemoteKeysDao().deleteRemoteKeys()
                    database.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = response.map {
                    StoryRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.storyRemoteKeysDao().insertAll(keys)
                database.storyDao().insertStory(response)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ListStory>): StoryRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.storyRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ListStory>): StoryRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.storyRemoteKeysDao().getRemoteKeysId(data.id)
        }
    }
}
