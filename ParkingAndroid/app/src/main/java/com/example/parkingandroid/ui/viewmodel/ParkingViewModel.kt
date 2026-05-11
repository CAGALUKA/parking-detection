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

    init {
        viewModelScope.launch {
            repository.getParkingStatus().collect {
                _spots.value = it
            }
        }
    }

    fun reserveSpot(id: Int) {
        repository.reserveSpot(id)
    }
}
