package jafar.stories.features.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import jafar.stories.data.model.AddStoryResponse
import jafar.stories.data.repository.Result
import jafar.stories.data.repository.StoryRepository
import jafar.stories.features.auth.utils.Dummy
import jafar.stories.features.auth.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
class AddStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repo: StoryRepository
    private lateinit var viewModel: AddStoryViewModel
    private val request = Dummy.generateDummyAddStoryRequest()

    @Before
    fun setUp() {
        viewModel = AddStoryViewModel(repo)
    }

    @Test
    fun `when addStory Should Not Null and Return Success`() = runTest {
        val expectedStories = MutableLiveData<Result<AddStoryResponse>>()
        expectedStories.value = Result.Success(Dummy.generateDummyAddStoryResponse())

        `when`(repo.addStory(request)).thenReturn(expectedStories)
        val actual = viewModel.doUpload(request).getOrAwaitValue()
        verify(repo).addStory(request)
        assertNotNull(actual)
        assertTrue(actual is Result.Success)
    }

    @Test
    fun `when addStory Network Error Should Return Error`() = runTest {
        val expectedStories = MutableLiveData<Result<AddStoryResponse>>()
        expectedStories.value = Result.Error("Error")

        `when`(repo.addStory(request)).thenReturn(expectedStories)
        val actual = viewModel.doUpload(request).getOrAwaitValue()
        verify(repo).addStory(request)
        assertNotNull(actual)
        assertTrue(actual is Result.Error)
    }
}