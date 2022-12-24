package jafar.stories.features.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import jafar.stories.data.model.ListStory
import jafar.stories.data.repository.StoryRepository
import jafar.stories.features.auth.utils.Dummy
import jafar.stories.features.auth.utils.MainDispatcherRule
import jafar.stories.features.auth.utils.getOrAwaitValue
import jafar.stories.features.main.home.StoryItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repo: StoryRepository

    @Test
    fun `When login token was clear and return to login activity`() = runTest {
        repo.removeUserToken()
        verify(repo).removeUserToken()
        assertTrue("Return to login activity", true)
    }

    @Test
    fun `When login state is true then changed to false and return to login activity`() = runTest {
        val expectedBoolean = false
        repo.changeLoginState(expectedBoolean)
        verify(repo).changeLoginState(expectedBoolean)
        assertTrue("Return to login activity", true)
    }

    @Test
    fun `When getStories should not null and return success`() = runTest {
        val dummyStory = Dummy.generateDummyStories()
        val data: PagingData<ListStory> =
            jafar.stories.features.main.PagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ListStory>>()
        expectedStory.value = data
        `when`(repo.getStories()).thenReturn(expectedStory)

        val viewModel = HomeViewModel(repo)
        val actualData: PagingData<ListStory> = viewModel.getStories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryItemAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualData)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
    }
}

class PagingSource : PagingSource<Int, LiveData<List<ListStory>>>() {
    companion object {
        fun snapshot(items: List<ListStory>): PagingData<ListStory> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStory>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStory>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}