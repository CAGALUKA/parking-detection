package com.example.parkingandroid.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class SpotStatusResponse(
    val spotId: Int,
    val occupied: Boolean,
    val label: String
)

data class DetectionResult(
    val totalSpots: Int,
    val occupied: Int,
    val empty: Int,
    val occupancyRate: Double,
    val vehicleCount: Int,
    val spots: List<SpotStatusResponse>,
    val timestampSeconds: Double,
    val progressPercent: Double,
    val videoDuration: Double,
    val frameIndex: Int,
    val processedAt: String
)

class ParkingApiService(
    private val baseUrl: String = "http://10.0.2.2:8000"
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    fun streamParkingStatus(
        filename: String = "demo1.mp4",
        intervalSeconds: Float = 15f
    ): Flow<DetectionResult> = flow {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/detect/stream/$filename?interval=$intervalSeconds")
            .addHeader("Accept", "text/event-stream")
            .build()

        val response = client.newCall(request).execute()
        val source = response.body?.source() ?: return@flow

        while (!source.exhausted()) {
            val line = source.readUtf8Line() ?: break
            if (!line.startsWith("data: ")) continue

            val json = line.removePrefix("data: ").trim()
            try {
                val obj = JSONObject(json)
                if (obj.optString("status") == "completed") break

                val spotsArray = obj.getJSONArray("spots")
                val spots = (0 until spotsArray.length()).map { i ->
                    val s = spotsArray.getJSONObject(i)
                    SpotStatusResponse(
                        spotId = s.getInt("spot_id"),
                        occupied = s.getBoolean("occupied"),
                        label = s.getString("label")
                    )
                }

                emit(DetectionResult(
                    totalSpots = obj.getInt("total_spots"),
                    occupied = obj.getInt("occupied"),
                    empty = obj.getInt("empty"),
                    occupancyRate = obj.getDouble("occupancy_rate"),
                    vehicleCount = obj.getInt("vehicle_count"),
                    spots = spots,
                    timestampSeconds = obj.getDouble("timestamp_seconds"),
                    progressPercent = obj.getDouble("progress_percent"),
                    videoDuration = obj.getDouble("video_duration"),
                    frameIndex = obj.getInt("frame_index"),
                    processedAt = obj.optString("processed_at", "")
                ))
            } catch (_: Exception) {
                // parse hatası — geç (e yerine _ yazılarak uyarı giderildi)
            }
        }
    }
}