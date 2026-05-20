package com.example.parkingandroid.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(72.dp)) {
        val w = size.width
        val h = size.height

        drawRoundRect(
            color = Color(0xFF1F5EFF),
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(w * 0.18f, h * 0.18f)
        )

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.23f, h * 0.18f),
            size = Size(w * 0.16f, h * 0.64f),
            cornerRadius = CornerRadius(20f, 20f)
        )

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.23f, h * 0.18f),
            size = Size(w * 0.40f, h * 0.18f),
            cornerRadius = CornerRadius(20f, 20f)
        )

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.47f, h * 0.22f),
            size = Size(w * 0.17f, h * 0.24f),
            cornerRadius = CornerRadius(20f, 20f)
        )

        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.23f, h * 0.42f),
            size = Size(w * 0.40f, h * 0.05f),
            cornerRadius = CornerRadius(10f, 10f)
        )

        drawRoundRect(
            color = Color(0xFF1F5EFF),
            topLeft = Offset(w * 0.36f, h * 0.26f),
            size = Size(w * 0.17f, h * 0.12f),
            cornerRadius = CornerRadius(14f, 14f)
        )

        drawLine(
            color = Color(0xFFFF6A2B),
            start = Offset(w * 0.75f, h * 0.20f),
            end = Offset(w * 0.75f, h * 0.80f),
            strokeWidth = w * 0.06f
        )

        drawCircle(
            color = Color(0xFFFF6A2B),
            radius = w * 0.055f,
            center = Offset(w * 0.75f, h * 0.80f)
        )

        drawRoundRect(
            color = Color.White.copy(alpha = 0.18f),
            topLeft = Offset(w * 0.06f, h * 0.06f),
            size = Size(w * 0.88f, h * 0.88f),
            cornerRadius = CornerRadius(w * 0.14f, h * 0.14f),
            style = Stroke(width = w * 0.02f)
        )
    }
}