package jafar.stories.features.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import jafar.stories.data.model.LoginResult
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repo: AuthRepository
    private lateinit var viewModel: LoginViewModel
    private val dummyLoginResponse = Dummy.generateDummyLoginResponse()
    private val dummyLoginRequest = Dummy.generateDummyLoginRequest()

    @Before
    fun before() {
        viewModel = LoginViewModel(repo)
    }

    @Test
    fun `When login success`() = runTest {
        val expectedLogin = MutableLiveData<Result<LoginResult>>()
        expectedLogin.value = Result.Success(dummyLoginResponse)

        `when`(repo.doLogin(dummyLoginRequest)).thenReturn(expectedLogin)
        val actualData = viewModel.doLogin(dummyLoginRequest).getOrAwaitValue()
        verify(repo).doLogin(dummyLoginRequest)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Success)
    }

    @Test
    fun `When login error`() = runTest {
        val expectedLogin = MutableLiveData<Result<LoginResult>>()
        expectedLogin.value = Result.Error("Error")

        `when`(repo.doLogin(dummyLoginRequest)).thenReturn(expectedLogin)
        val actualData = viewModel.doLogin(dummyLoginRequest).getOrAwaitValue()
        verify(repo).doLogin(dummyLoginRequest)
        assertNotNull(actualData)
        assertTrue(actualData is Result.Error)
    }
}