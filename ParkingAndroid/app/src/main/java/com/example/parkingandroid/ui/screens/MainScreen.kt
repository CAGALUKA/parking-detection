package com.example.parkingandroid.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkingandroid.data.model.ParkingSpot
import com.example.parkingandroid.data.model.SpotStatus

@Composable
fun MainScreen(
    spots: List<ParkingSpot>,
    onSpotClick: (Int) -> Unit,
    hasActiveReservation: Boolean = false,
    currentReservationSpotId: Int? = null
) {
    val occupiedCount = spots.count { it.status == SpotStatus.OCCUPIED }
    val availableCount = spots.count { it.status == SpotStatus.AVAILABLE }
    val reservedCount = spots.count { it.status == SpotStatus.RESERVED }
    val totalCount = spots.size
    val occupancyRate = if (totalCount == 0) 0 else ((occupiedCount + reservedCount) * 100) / totalCount

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F6FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Parking Lot",
                color = Color(0xFF081B3A),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Choose one green car to make a reservation.",
                color = Color(0xFF667085),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                ParkingStatCard(
                    title = "Full",
                    value = occupiedCount.toString(),
                    color = Color(0xFFE53935),
                    modifier = Modifier.weight(1f)
                )

                ParkingStatCard(
                    title = "Empty",
                    value = availableCount.toString(),
                    color = Color(0xFF03A94D),
                    modifier = Modifier.weight(1f)
                )

                ParkingStatCard(
                    title = "Reserved",
                    value = reservedCount.toString(),
                    color = Color(0xFFFFB300),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Occupancy Rate",
                        color = Color(0xFF344054),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "$occupancyRate%",
                        color = Color(0xFFFF4B00),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            if (hasActiveReservation) {
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF1E8), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "You already have an active reservation. Each user can reserve only one parking spot.",
                        color = Color(0xFF9A3412),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D3748)),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    val roadHeight = 22.dp
                    val rowGap = 6.dp
                    val rowHeight = (maxHeight - roadHeight * 3 - rowGap * 6) / 4f

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        ParkingRow(
                            spots = spots.takeRange(0, 3),
                            rowHeight = rowHeight,
                            onSpotClick = onSpotClick,
                            hasActiveReservation = hasActiveReservation,
                            currentReservationSpotId = currentReservationSpotId
                        )

                        Road(height = roadHeight)

                        ParkingRow(
                            spots = spots.takeRange(3, 6),
                            rowHeight = rowHeight,
                            onSpotClick = onSpotClick,
                            hasActiveReservation = hasActiveReservation,
                            currentReservationSpotId = currentReservationSpotId
                        )

                        Road(height = roadHeight)

                        ParkingRow(
                            spots = spots.takeRange(6, 9),
                            rowHeight = rowHeight,
                            onSpotClick = onSpotClick,
                            hasActiveReservation = hasActiveReservation,
                            currentReservationSpotId = currentReservationSpotId
                        )

                        Road(height = roadHeight)

                        ParkingRow(
                            spots = spots.takeRange(9, 12),
                            rowHeight = rowHeight,
                            onSpotClick = onSpotClick,
                            hasActiveReservation = hasActiveReservation,
                            currentReservationSpotId = currentReservationSpotId
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(Color(0xFF03A94D), "Empty")
                LegendItem(Color(0xFFFFC107), "Reserved")
                LegendItem(Color(0xFFE53935), "Full")
            }
        }
    }
}

private fun List<ParkingSpot>.takeRange(start: Int, end: Int): List<ParkingSpot> {
    if (this.isEmpty()) return emptyList()
    return this.drop(start).take(end - start)
}

@Composable
private fun ParkingRow(
    spots: List<ParkingSpot>,
    rowHeight: Dp,
    onSpotClick: (Int) -> Unit,
    hasActiveReservation: Boolean,
    currentReservationSpotId: Int?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        spots.forEach { spot ->
            ParkingCarItem(
                spot = spot,
                onSpotClick = onSpotClick,
                hasActiveReservation = hasActiveReservation,
                isCurrentReservation = currentReservationSpotId == spot.id,
                modifier = Modifier.weight(1f)
            )
        }

        repeat(3 - spots.size) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ParkingCarItem(
    spot: ParkingSpot,
    onSpotClick: (Int) -> Unit,
    hasActiveReservation: Boolean,
    isCurrentReservation: Boolean,
    modifier: Modifier = Modifier
) {
    val carColor = when (spot.status) {
        SpotStatus.AVAILABLE -> Color(0xFF03A94D)
        SpotStatus.RESERVED -> Color(0xFFFFC107)
        SpotStatus.OCCUPIED -> Color(0xFFE53935)
    }

    val clickable = (spot.status == SpotStatus.AVAILABLE && !hasActiveReservation) || isCurrentReservation

    Column(
        modifier = modifier
            .alpha(if (clickable || isCurrentReservation) 1f else 0.82f)
            .clickable(enabled = clickable) {
                onSpotClick(spot.id)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF1F2937), RoundedCornerShape(12.dp))
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            CarShape(color = carColor)

            if (isCurrentReservation) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color(0xFF03A94D),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "B${spot.id}",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = when (spot.status) {
                SpotStatus.AVAILABLE -> "EMPTY"
                SpotStatus.RESERVED -> "RESERVED"
                SpotStatus.OCCUPIED -> "FULL"
            },
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 7.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun CarShape(color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.20f, h * 0.14f),
            size = androidx.compose.ui.geometry.Size(w * 0.60f, h * 0.56f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
        )

        drawRoundRect(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(w * 0.31f, h * 0.24f),
            size = androidx.compose.ui.geometry.Size(w * 0.38f, h * 0.17f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(9f, 9f)
        )

        drawCircle(
            color = Color(0xFF111827),
            radius = w * 0.070f,
            center = Offset(w * 0.30f, h * 0.74f)
        )

        drawCircle(
            color = Color(0xFF111827),
            radius = w * 0.070f,
            center = Offset(w * 0.70f, h * 0.74f)
        )

        drawCircle(
            color = Color.White,
            radius = w * 0.024f,
            center = Offset(w * 0.30f, h * 0.74f)
        )

        drawCircle(
            color = Color.White,
            radius = w * 0.024f,
            center = Offset(w * 0.70f, h * 0.74f)
        )
    }
}

@Composable
private fun Road(height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(Color(0xFF111827), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(5) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(2.dp)
                        .background(Color.White.copy(alpha = 0.55f), RoundedCornerShape(50.dp))
                )
            }
        }
    }
}

@Composable
private fun ParkingStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color(0xFF667085),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = value,
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = label,
            color = Color(0xFF344054),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}