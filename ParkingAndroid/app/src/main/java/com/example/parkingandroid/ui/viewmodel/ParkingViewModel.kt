package com.example.parkingandroid.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingandroid.data.model.ParkingSpot
import com.example.parkingandroid.data.repository.ParkingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParkingViewModel : ViewModel() {
    private val repository = ParkingRepository()
    private val _spots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val spots: StateFlow<List<ParkingSpot>> = _spots.asStateFlow()

    private val _activeReservationSpotId = MutableStateFlow<Int?>(null)
    val activeReservationSpotId: StateFlow<Int?> = _activeReservationSpotId.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(15 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            repository.getParkingStatus().collect {
                _spots.value = it
            }
        }
    }

    fun reserveSpot(id: Int) {
        if (_activeReservationSpotId.value == null) {
            repository.reserveSpot(id)
            _activeReservationSpotId.value = id
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _remainingSeconds.value = 15 * 60
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                kotlinx.coroutines.delay(1000)
                _remainingSeconds.value -= 1
            }
        }
    }

    fun confirmArrival() {
        _activeReservationSpotId.value?.let { id ->
            repository.occupySpot(id)
            _activeReservationSpotId.value = null
            timerJob?.cancel()
        }
    }

    fun cancelReservation() {
        _activeReservationSpotId.value?.let { id ->
            repository.releaseSpot(id)
            _activeReservationSpotId.value = null
            timerJob?.cancel()
        }
    }
}
