package jafar.stories.features.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import jafar.stories.data.model.DataRegisterResponse
import jafar.stories.data.repository.AuthRepository
import jafar.stories.data.repository.Result
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
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repo: AuthRepository
    private lateinit var viewModel: RegisterViewModel
    private val dummyRegisterResponse = Dummy.generateDummyRegisterResponse()
    private val dummyRegisterRequest = Dummy.generateDummyRegisterRequest()

    @Before
    fun before() {
        viewModel = RegisterViewModel(repo)
    }

    @Test
    fun `When register success`() = runTest {
        val expectedRegister = MutableLiveData<Result<DataRegisterResponse>>()
        expectedRegister.value = Result.Success(dummyRegisterResponse)

        Mockito.`when`(repo.doRegister(dummyRegisterRequest)).thenReturn(expectedRegister)
        val actualData = viewModel.doRegister(dummyRegisterRequest).getOrAwaitValue()
        Mockito.verify(repo).doRegister(dummyRegisterRequest)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Success)
    }

    @Test
    fun `When register error`() = runTest {
        val expectedRegister = MutableLiveData<Result<DataRegisterResponse>>()
        expectedRegister.value = Result.Error("Error")

        Mockito.`when`(repo.doRegister(dummyRegisterRequest)).thenReturn(expectedRegister)
        val actualData = viewModel.doRegister(dummyRegisterRequest).getOrAwaitValue()
        Mockito.verify(repo).doRegister(dummyRegisterRequest)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Error)
    }
}