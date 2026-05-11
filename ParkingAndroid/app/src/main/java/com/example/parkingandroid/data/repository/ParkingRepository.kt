package com.example.parkingandroid.data.repository

import com.example.parkingandroid.data.model.ParkingSpot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ParkingRepository {
    // 12 slotluk sabit veri seti
    private val _spots = MutableStateFlow(List(12) { index ->
        val id = index + 1
        // Başlangıçta 1, 3, 7 ve 10 numaralı yerler dolu olsun
        val initialFull = when (id) {
            1, 3, 7, 10 -> true
            else -> false
        }
        ParkingSpot(id = id, isFull = initialFull)
    })

    fun getParkingStatus(): Flow<List<ParkingSpot>> = _spots

    fun reserveSpot(id: Int) {
        val currentList = _spots.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1 && !currentList[index].isFull) {
            // Sadece boş olan yerleri rezerve edebiliriz
            currentList[index] = currentList[index].copy(isFull = true)
            _spots.value = currentList
        }
    }
}
