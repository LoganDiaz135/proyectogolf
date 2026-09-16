package com.example.borradordegolf.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuScreen(
    onJugar: () -> Unit,
    onNiveles: () -> Unit,
    onComoJugar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorForestDark)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GolfHeroIcon()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "MINI GOLF PRO",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = ColorCream,
                shadow = Shadow(color = ColorGold.copy(alpha = 0.55f), offset = Offset(0f, 4f), blurRadius = 10f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(3.dp)
                .background(ColorGold)
        )

        Spacer(modifier = Modifier.height(56.dp))

        Button(
            onClick = onJugar,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorGold,
                contentColor = ColorForestDark
            )
        ) {
            Text("JUGAR", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNiveles,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorCream)
        ) {
            Text("NIVELES", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onComoJugar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorCream)
        ) {
            Text("CÓMO JUGAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun GolfHeroIcon() {
    Canvas(modifier = Modifier.size(120.dp)) {
        val center = Offset(size.width / 2f, size.height * 0.75f)

        drawOval(
            color = Color.Black.copy(alpha = 0.25f),
            topLeft = Offset(center.x - 30f, center.y + 10f),
            size = Size(60f, 14f)
        )

        val poleTop = Offset(center.x, center.y - size.height * 0.6f)
        drawLine(
            color = ColorCream,
            start = center,
            end = poleTop,
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )

        val flagPath = Path().apply {
            moveTo(poleTop.x, poleTop.y)
            lineTo(poleTop.x + 40f, poleTop.y + 14f)
            lineTo(poleTop.x, poleTop.y + 28f)
            close()
        }
        drawPath(path = flagPath, color = ColorGold)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color(0xFFDDDDDD)),
                center = center - Offset(6f, 6f),
                radius = 26f
            ),
            radius = 18f,
            center = center
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = 5f,
            center = center - Offset(6f, 6f)
        )
    }
}