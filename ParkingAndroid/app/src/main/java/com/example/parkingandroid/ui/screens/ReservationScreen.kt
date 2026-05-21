package com.example.parkingandroid.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ReservationStatusScreen(
    spotId: Int,
    remainingSeconds: Int,
    onBackToMap: () -> Unit,
    onConfirmArrival: () -> Unit = {}
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timerText = "%02d:%02d".format(minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(43.dp)
                .background(Color(0xFFFF4B00)),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "Reservation Confirmation & Status",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 22.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(9.dp, RoundedCornerShape(14.dp))
                    .background(Color.White, RoundedCornerShape(14.dp))
                    .padding(horizontal = 22.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .background(Color(0xFFD9FBE4), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CheckIcon()
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Booking Confirmed!",
                    color = Color(0xFF081B3A),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your parking spot is reserved",
                    color = Color(0xFF344054),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                InfoRow(label = "Spot Number", value = "B$spotId")

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(label = "Floor", value = "Level 1")

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(label = "Status", value = "Reserved")

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFFFFB878),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(Color(0xFFFFF7ED), RoundedCornerShape(12.dp))
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ClockIcon()

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Reservation Timer",
                            color = Color(0xFF8C2400),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(9.dp))

                    Text(
                        text = timerText,
                        color = Color(0xFFFF4B00),
                        fontSize = 33.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Time remaining to arrive",
                        color = Color(0xFFFF4B00),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onConfirmArrival()
                        onBackToMap()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Confirm Vehicle Arrival",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onBackToMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1F5EFF),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Back to Parking Lot",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(Color(0xFF03A94D), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(10.dp)
                        .background(Color.White, CircleShape)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Push Notification",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Spot B$spotId confirmed. Welcome to our parking lot!",
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(Color(0xFFF8F9FB), RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF344054),
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            color = Color(0xFF081B3A),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CheckIcon() {
    Canvas(modifier = Modifier.size(48.dp)) {
        drawCircle(
            color = Color(0xFF08A84D),
            radius = size.minDimension / 2.6f,
            style = Stroke(width = 4.dp.toPx())
        )

        drawLine(
            color = Color(0xFF08A84D),
            start = Offset(size.width * 0.28f, size.height * 0.52f),
            end = Offset(size.width * 0.44f, size.height * 0.68f),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color(0xFF08A84D),
            start = Offset(size.width * 0.44f, size.height * 0.68f),
            end = Offset(size.width * 0.76f, size.height * 0.34f),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ClockIcon() {
    Canvas(modifier = Modifier.size(23.dp)) {
        drawCircle(
            color = Color(0xFFFF4B00),
            radius = size.minDimension / 2.3f,
            style = Stroke(width = 2.dp.toPx())
        )

        drawLine(
            color = Color(0xFFFF4B00),
            start = Offset(size.width / 2f, size.height / 2f),
            end = Offset(size.width / 2f, size.height * 0.30f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color(0xFFFF4B00),
            start = Offset(size.width / 2f, size.height / 2f),
            end = Offset(size.width * 0.67f, size.height * 0.60f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}