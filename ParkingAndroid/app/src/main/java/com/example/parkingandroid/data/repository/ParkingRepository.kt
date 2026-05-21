package com.example.parkingandroid.data.repository

import com.example.parkingandroid.data.model.ParkingSpot
import com.example.parkingandroid.data.model.SpotStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ParkingRepository {
    // 12 slotluk sabit veri seti
    private val _spots = MutableStateFlow(List(12) { index ->
        val id = index + 1
        // Başlangıçta 1, 3, 7 ve 10 numaralı yerler dolu (OCCUPIED) olsun
        val initialStatus = when (id) {
            1, 3, 7, 10 -> SpotStatus.OCCUPIED
            else -> SpotStatus.AVAILABLE
        }
        ParkingSpot(id = id, status = initialStatus)
    })

    fun getParkingStatus(): Flow<List<ParkingSpot>> = _spots

    fun reserveSpot(id: Int) {
        updateSpotStatus(id, SpotStatus.RESERVED)
    }

    fun occupySpot(id: Int) {
        updateSpotStatus(id, SpotStatus.OCCUPIED)
    }

    fun releaseSpot(id: Int) {
        updateSpotStatus(id, SpotStatus.AVAILABLE)
    }

    private fun updateSpotStatus(id: Int, status: SpotStatus) {
        val currentList = _spots.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(status = status)
            _spots.value = currentList
        }
    }
}
