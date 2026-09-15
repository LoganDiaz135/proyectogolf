package com.example.borradordegolf.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GolfScreen(viewModel: GolfViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    var isDebugMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4C3)) // Color crema de fondo
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏆 Mini Golf Pro",
            modifier = Modifier.clickable { isDebugMode = !isDebugMode }, // Click en título activa debug
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF388E3C),
                shadow = Shadow(color = Color.Gray, offset = Offset(2f, 2f), blurRadius = 4f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isDebugMode) {
            Text("DEBUG MODE ON - Tap the field to simulate a shot", color = Color.Red, fontSize = 10.sp)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(label = "Strokes", value = state.strokeCount.toString())
                StatItem(label = "Hole", value = "1")
                StatItem(label = "Par", value = "3")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF66BB6A), Color(0xFF388E3C))
                    )
                )
                .clickable(enabled = isDebugMode) {
                    // Simular golpe en la dirección que estemos apuntando
                    viewModel.onSwingDetected(40f)
                }
        ) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()

            // Animación de la pelota
            val targetOffset = Offset(
                (state.ballPosition.x / 100f) * width,
                (state.ballPosition.y / 100f) * height
            )

            val animatedBallOffset by animateOffsetAsState(
                targetValue = targetOffset,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "BallMovement"
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                // Dibujar Flecha de Dirección (Giroscopio)
                if (!state.isHoleCompleted) {
                    val arrowLength = 60.dp.toPx()
                    val arrowEndX = animatedBallOffset.x + arrowLength * kotlin.math.cos(state.aimingDirectionRad)
                    val arrowEndY = animatedBallOffset.y + arrowLength * kotlin.math.sin(state.aimingDirectionRad)

                    drawLine(
                        color = Color.White.copy(alpha = 0.7f),
                        start = animatedBallOffset,
                        end = Offset(arrowEndX, arrowEndY),
                        strokeWidth = 4.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )

                    // Cabeza de la flecha
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(arrowEndX, arrowEndY)
                    )
                }

                // Dibujar Hoyo
                val holeX = (state.holePosition.x / 100f) * width
                val holeY = (state.holePosition.y / 100f) * height

                // Sombra del hoyo
                drawCircle(
                    color = Color.Black.copy(alpha = 0.3f),
                    radius = 24.dp.toPx(),
                    center = Offset(holeX, holeY)
                )
                // Agujero
                drawCircle(
                    color = Color(0xFF1B5E20),
                    radius = 20.dp.toPx(),
                    center = Offset(holeX, holeY)
                )

                // Bandera del hoyo
                drawLine(
                    color = Color.White,
                    start = Offset(holeX, holeY),
                    end = Offset(holeX, holeY - 40.dp.toPx()),
                    strokeWidth = 3.dp.toPx()
                )
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(holeX, holeY - 40.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 15.dp.toPx())
                )

                // Dibujar Pelota con su posición animada
                // Sombra de la pelota
                drawCircle(
                    color = Color.Black.copy(alpha = 0.2f),
                    radius = 13.dp.toPx(),
                    center = animatedBallOffset + Offset(2f, 2f)
                )
                // Pelota
                drawCircle(
                    color = Color.White,
                    radius = 12.dp.toPx(),
                    center = animatedBallOffset
                )
            }

            if (state.isHoleCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AWESOME!\nHole Completed",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Yellow,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.lastSwingForce > 0) {
            LinearProgressIndicator(
                progress = { (state.lastSwingForce / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFFFC107),
                trackColor = Color.LightGray
            )
            Text(
                text = "Last shot: ${"%.0f".format(state.lastSwingForce)}%",
                style = MaterialTheme.typography.labelSmall
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.resetGame() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Restart", color = Color.White)
            }
        }

        Text(
            text = "💡 Tip: Swing faster to hit farther.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 12.dp),
            color = Color.DarkGray
        )
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}