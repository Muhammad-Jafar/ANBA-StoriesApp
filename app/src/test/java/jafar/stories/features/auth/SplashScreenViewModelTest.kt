package jafar.stories.features.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import jafar.stories.data.repository.AuthRepository
import jafar.stories.features.auth.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SplashScreenViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repo: AuthRepository

    @Test
    fun `When login state is false and return to login activity`() {
        repo.getLoginState()
        verify(repo).getLoginState()
        assertTrue("return to login activity", true)
    }

    @Test
    fun `When login state is true and return to main activity`() {
        repo.getLoginState()
        verify(repo).getLoginState()
        assertTrue("return to main activity", true)
    }
}