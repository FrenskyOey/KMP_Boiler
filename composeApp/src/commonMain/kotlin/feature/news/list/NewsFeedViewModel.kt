package feature.news.list

import androidx.lifecycle.ViewModel
import feature.news.domain.usecase.GetNewsFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewsFeedViewModel(
    private val getNewsFeedUseCase: GetNewsFeedUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow("Loading")
    val uiState: StateFlow<String> = _uiState
    
    // Placeholder logic
}
