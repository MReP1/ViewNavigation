package little.goose.navigation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {

    val countStateFlow = flow {
        var count = 0L
        while (count < 71717171L) {
            delay(500L)
            emit(count++)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0L)

}