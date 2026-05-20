package com.example.parkingandroid.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2200)
        onTimeout()
    }

    val transition = rememberInfiniteTransition(label = "SplashAnimation")
    val logoScale by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoScale"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F6FA)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6FA)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(124.dp)
                        .scale(logoScale)
                        .background(Color(0xFFE8F1FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    AppLogo(modifier = Modifier.size(82.dp))
                }

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = "PARK404",
                    color = Color(0xFF0A1F44),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Smart Parking Reservation System",
                    color = Color(0xFF5D6B82),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(34.dp))

                CircularProgressIndicator(
                    color = Color(0xFFFF4B00),
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(34.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Preparing parking spaces...",
                    color = Color(0xFF7A8495),
                    fontSize = 13.sp,
                    modifier = Modifier.alpha(0.9f)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 34.dp)
                    .background(Color.White, RoundedCornerShape(50.dp))
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Text(
                    text = "Fast • Simple • Secure",
                    color = Color(0xFF0A1F44),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}