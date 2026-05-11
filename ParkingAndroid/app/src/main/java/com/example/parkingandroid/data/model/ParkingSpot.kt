package com.example.parkingandroid.data.model

enum class SpotStatus {
    AVAILABLE, // Boş (Yeşil)
    OCCUPIED,  // Dolu (Kırmızı)
    RESERVED   // Rezerve Edildi (Sarı)
}

data class ParkingSpot(
    val id: Int,
    val status: SpotStatus
)
