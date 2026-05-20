package com.example.parkingandroid.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    totalSpots: Int = 50,
    availableSpots: Int = 47,
    reservationCount: Int = 3,
    onShowParkingClick: () -> Unit = {}
) {
    val occupiedSpots = totalSpots - availableSpots
    val occupancyRate = if (totalSpots == 0) 0 else (occupiedSpots * 100) / totalSpots

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F6FA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AppLogo(modifier = Modifier.size(58.dp))

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "PARK404",
                        color = Color(0xFF081B3A),
                        fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Smart Parking System",
                        color = Color(0xFF667085),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    Text(
                        text = "Find your parking spot faster.",
                        color = Color(0xFF081B3A),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 33.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Check available places, reserve a spot, and reach the parking area before your timer ends.",
                        color = Color(0xFF667085),
                        fontSize = 15.sp,
                        lineHeight = 21.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(118.dp)
                            .background(Color(0xFFF8FAFF), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        MiniParkingIllustration()
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HomeCounterCard(
                    title = "Occupancy",
                    value = "$occupancyRate%",
                    subtitle = "$occupiedSpots / $totalSpots full",
                    color = Color(0xFFFF4B00),
                    modifier = Modifier.weight(1f)
                )

                HomeCounterCard(
                    title = "Available",
                    value = availableSpots.toString(),
                    subtitle = "spots ready",
                    color = Color(0xFF03A94D),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            HomeCounterCard(
                title = "Reservations Today",
                value = reservationCount.toString(),
                subtitle = "active reservations made by users",
                color = Color(0xFF1F5EFF),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = "How it works?",
                    color = Color(0xFF081B3A),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                IntroBullet("1", "Green car means the spot is available.")
                IntroBullet("2", "Yellow car means the spot is reserved.")
                IntroBullet("3", "Red car means the spot is occupied.")
                IntroBullet("4", "Each user can have only one active reservation.")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onShowParkingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1F5EFF),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "View Parking Lot",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HomeCounterCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color(0xFF667085),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                color = color,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color(0xFF98A2B3),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun IntroBullet(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(25.dp)
                .background(Color(0xFFE8F1FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color(0xFF1F5EFF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            color = Color(0xFF344054),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun MiniParkingIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = Color(0xFFE9EDF5),
            topLeft = Offset(w * 0.05f, h * 0.42f),
            size = androidx.compose.ui.geometry.Size(w * 0.90f, h * 0.16f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
        )

        fun car(cx: Float, cy: Float, color: Color) {
            drawRoundRect(
                color = color,
                topLeft = Offset(cx - w * 0.08f, cy - h * 0.12f),
                size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.20f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = 4f,
                center = Offset(cx - w * 0.045f, cy + h * 0.10f)
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = 4f,
                center = Offset(cx + w * 0.045f, cy + h * 0.10f)
            )
        }

        car(w * 0.20f, h * 0.26f, Color(0xFF03A94D))
        car(w * 0.43f, h * 0.26f, Color(0xFFFFC107))
        car(w * 0.66f, h * 0.26f, Color(0xFFE53935))
        car(w * 0.32f, h * 0.76f, Color(0xFF03A94D))
        car(w * 0.56f, h * 0.76f, Color(0xFF03A94D))
        car(w * 0.79f, h * 0.76f, Color(0xFFFFC107))
    }
}