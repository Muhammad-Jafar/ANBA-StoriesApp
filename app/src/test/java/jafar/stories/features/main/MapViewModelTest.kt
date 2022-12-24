package jafar.stories.features.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import jafar.stories.data.model.StoryResponse
import jafar.stories.data.repository.Result
import jafar.stories.data.repository.StoryRepository
import jafar.stories.features.auth.utils.Dummy
import jafar.stories.features.auth.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repo: StoryRepository
    private lateinit var viewModel: MapViewModel
    private val dummyStory = Dummy.generateDummyStoriesEntity()

    @Before
    fun setUp() {
        viewModel = MapViewModel(repo)
    }

    @Test
    fun `when getMapStories Should Not Null and Return Success`() = runTest {
        val expectedStories = MutableLiveData<Result<StoryResponse>>()
        expectedStories.value = Result.Success(dummyStory)
        `when`(repo.getStoriesLocation()).thenReturn(expectedStories)
        val actual = viewModel.getStoriesLocation().getOrAwaitValue()
        verify(repo).getStoriesLocation()
        assertNotNull(actual)
        assertTrue(actual is Result.Success)
        assertEquals(dummyStory.listStory.size, (actual as Result.Success).data.listStory.size)
    }

    @Test
    fun `when getMapStories Network Error Should Return Error`() = runTest {
        val expectedStories = MutableLiveData<Result<StoryResponse>>()
        expectedStories.value = Result.Error("Error")
        `when`(repo.getStoriesLocation()).thenReturn(expectedStories)
        val actual = viewModel.getStoriesLocation().getOrAwaitValue()
        verify(repo).getStoriesLocation()
        assertNotNull(actual)
        assertTrue(actual is Result.Error)
    }
}