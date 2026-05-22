package com.example.parkingandroid.data.repository

import com.example.parkingandroid.data.model.ParkingSpot
import com.example.parkingandroid.data.model.SpotStatus
import com.example.parkingandroid.data.remote.DetectionResult
import com.example.parkingandroid.data.remote.ParkingApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ParkingRepository {

    private val apiService = ParkingApiService()

    private val _spots = MutableStateFlow(List(12) { index ->
        val id = index + 1
        val initialStatus = when (id) {
            1, 3, 7, 10 -> SpotStatus.OCCUPIED
            else -> SpotStatus.AVAILABLE
        }
        ParkingSpot(id = id, status = initialStatus)
    })

    fun getParkingStatus(): Flow<List<ParkingSpot>> = _spots

    // ── Yeni: backend stream ──────────────────────────────────────────────
    fun streamFromBackend(
        filename: String = "demo1.mp4",
        intervalSeconds: Float = 15f
    ): Flow<DetectionResult> = apiService.streamParkingStatus(filename, intervalSeconds)

    fun updateSpotsFromDetection(result: DetectionResult) {
        _spots.value = result.spots.map { s ->
            ParkingSpot(
                id = s.spotId, // s.spot_id yerine s.spotId yapıldı
                status = if (s.occupied) SpotStatus.OCCUPIED else SpotStatus.AVAILABLE
            )
        }
    }
    // ─────────────────────────────────────────────────────────────────────

    fun reserveSpot(id: Int) = updateSpotStatus(id, SpotStatus.RESERVED)
    fun occupySpot(id: Int) = updateSpotStatus(id, SpotStatus.OCCUPIED)
    fun releaseSpot(id: Int) = updateSpotStatus(id, SpotStatus.AVAILABLE)

    private fun updateSpotStatus(id: Int, status: SpotStatus) {
        val currentList = _spots.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(status = status)
            _spots.value = currentList
        }
    }
}
