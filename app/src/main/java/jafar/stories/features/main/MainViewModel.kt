package jafar.stories.features.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import jafar.stories.data.model.AddStoryRequest
import jafar.stories.data.model.ListStory
import jafar.stories.data.repository.StoryRepository
import kotlinx.coroutines.launch

open class MainViewModel : ViewModel()

class HomeViewModel(private val repo: StoryRepository) : MainViewModel() {
    val getStories: LiveData<PagingData<ListStory>> = repo.getStories().cachedIn(viewModelScope)
    fun removeUserToken() = viewModelScope.launch { repo.removeUserToken() }
    fun changeLoginState(isLogin: Boolean) =
        viewModelScope.launch { repo.changeLoginState(isLogin) }
}

class MapViewModel(private val repo: StoryRepository) : MainViewModel() {
    fun getStoriesLocation() = repo.getStoriesLocation()
}

class AddStoryViewModel(private val repo: StoryRepository) : MainViewModel() {
    val isLocationPicked = MutableLiveData(false)
    val latitude = MutableLiveData(0.0)
    val longitude = MutableLiveData(0.0)

    fun doUpload(request: AddStoryRequest) = repo.addStory(request)
}