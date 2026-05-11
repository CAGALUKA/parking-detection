package com.example.parkingandroid.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
    onBackToMap: () -> Unit
) {
    var secondsLeft by remember { mutableStateOf(15 * 60) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timerText = "%02d:%02d".format(minutes, seconds)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFD9FBE4), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CheckIcon()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Rezervasyon Onaylandı!",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Park yeriniz sizin için ayrıldı.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            InfoCard(label = "Park No", value = "#$spotId")
            Spacer(modifier = Modifier.height(16.dp))
            InfoCard(label = "Kat", value = "Zemin Kat")

            Spacer(modifier = Modifier.height(32.dp))

            // Timer Bölümü
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = Color(0xFFFFF7ED),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB878))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ClockIcon()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kalan Süre",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF8C2400)
                        )
                    }

                    Text(
                        text = timerText,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp),
                        color = Color(0xFFFF4A00)
                    )

                    Text(
                        text = "Lütfen süre dolmadan alana giriş yapın",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF4A00).copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackToMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Haritaya Dön", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(value, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun CheckIcon() {
    Canvas(modifier = Modifier.size(56.dp)) {
        drawCircle(
            color = Color(0xFF08A84D),
            radius = size.minDimension / 2.5f,
            style = Stroke(width = 4.dp.toPx())
        )
        drawLine(
            color = Color(0xFF08A84D),
            start = Offset(size.width * 0.3f, size.height * 0.5f),
            end = Offset(size.width * 0.45f, size.height * 0.65f),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF08A84D),
            start = Offset(size.width * 0.45f, size.height * 0.65f),
            end = Offset(size.width * 0.7f, size.height * 0.35f),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ClockIcon() {
    Canvas(modifier = Modifier.size(24.dp)) {
        drawCircle(
            color = Color(0xFFFF4A00),
            radius = size.minDimension / 2.2f,
            style = Stroke(width = 2.dp.toPx())
        )
        drawLine(
            color = Color(0xFFFF4A00),
            start = Offset(size.width / 2f, size.height / 2f),
            end = Offset(size.width / 2f, size.height * 0.3f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFFFF4A00),
            start = Offset(size.width / 2f, size.height / 2f),
            end = Offset(size.width * 0.7f, size.height * 0.6f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
