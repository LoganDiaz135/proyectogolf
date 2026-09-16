package com.example.borradordegolf.ui

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GolfScreen(
    viewModel: GolfViewModel,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var isDebugMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorForestDark)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = onBackToMenu) {
                Text("‹ Menú", color = ColorCream, fontWeight = FontWeight.Bold)
            }
        }

        GameTitle(onClick = { isDebugMode = !isDebugMode })

        Spacer(modifier = Modifier.height(12.dp))

        if (isDebugMode) {
            Text(
                "DEBUG MODE ON · toca el campo para simular un golpe",
                color = ColorGold,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        StatsPanel(
            strokeCount = state.strokeCount,
            levelNumber = state.currentLevelNumber,
            par = state.par
        )

        Spacer(modifier = Modifier.height(20.dp))

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(ColorForestDark)
                .clickable(enabled = isDebugMode) {
                    viewModel.onSwingDetected(40f)
                }
        ) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()

            val targetOffset = Offset(
                (state.ballPosition.x / 100f) * width,
                (state.ballPosition.y / 100f) * height
            )

            val animatedBallOffset by animateOffsetAsState(
                targetValue = targetOffset,
                animationSpec = tween(durationMillis = 16),
                label = "BallMovement"
            )

            val holeTarget = Offset(
                (state.holePosition.x / 100f) * width,
                (state.holePosition.y / 100f) * height
            )

            val obstacleRects = state.obstacles.map { obstacle ->
                Rect(
                    left = (obstacle.left / 100f) * width,
                    top = (obstacle.top / 100f) * height,
                    right = (obstacle.right / 100f) * width,
                    bottom = (obstacle.bottom / 100f) * height
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawField(this.size)

                obstacleRects.forEach { rect -> drawObstacle(rect) }

                drawFlag(holeTarget)
                drawHole(holeTarget)

                if (!state.isHoleCompleted) {
                    drawAimArrow(animatedBallOffset, state.aimingDirectionRad)
                }

                drawBall(animatedBallOffset)
            }

            if (state.isHoleCompleted) {
                VictoryOverlay(strokeCount = state.strokeCount)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (state.lastSwingForce > 0) {
            SwingForceIndicator(force = state.lastSwingForce)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = { viewModel.resetGame() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorPanelDark,
                contentColor = ColorGold
            )
        ) {
            Text("Reiniciar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Text(
            text = "Tip: un swing más rápido pega más fuerte",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 10.dp),
            color = ColorCream.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun GameTitle(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = "Mini Golf Pro",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = ColorCream,
                shadow = Shadow(color = ColorGold.copy(alpha = 0.5f), offset = Offset(0f, 3f), blurRadius = 8f)
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(56.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(ColorGold)
        )
    }
}

@Composable
private fun StatsPanel(strokeCount: Int, levelNumber: Int, par: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorPanelDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(label = "Golpes", value = strokeCount.toString(), highlight = true)
            StatDivider()
            StatItem(label = "Nivel", value = levelNumber.toString())
            StatDivider()
            StatItem(label = "Par", value = par.toString())
        }
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(ColorCream.copy(alpha = 0.15f))
    )
}

@Composable
fun StatItem(label: String, value: String, highlight: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) ColorGold else ColorCream
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = ColorCream.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SwingForceIndicator(force: Float) {
    Column {
        Text(
            text = "Último golpe: ${"%.0f".format(force)}",
            style = MaterialTheme.typography.labelSmall,
            color = ColorCream.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (force / 90f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = ColorGold,
            trackColor = ColorPanelDark
        )
    }
}

@Composable
private fun VictoryOverlay(strokeCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorForestDark.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ColorPanelDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡Hoyo completado!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorGold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$strokeCount golpes",
                    fontSize = 15.sp,
                    color = ColorCream.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ---- Dibujo del campo y elementos (Canvas) ----

private fun DrawScope.drawField(size: Size) {
    drawRoundRect(
        color = ColorForestDark,
        size = size,
        cornerRadius = CornerRadius(20f, 20f)
    )

    val inset = 14f
    val fieldTopLeft = Offset(inset, inset)
    val fieldSize = Size(size.width - inset * 2, size.height - inset * 2)

    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(ColorTurfPrimary, ColorTurfSecondary)
        ),
        topLeft = fieldTopLeft,
        size = fieldSize,
        cornerRadius = CornerRadius(14f, 14f)
    )

    val stripeCount = 8
    val stripeHeight = fieldSize.height / stripeCount
    for (i in 0 until stripeCount step 2) {
        drawRect(
            color = Color.Black.copy(alpha = 0.05f),
            topLeft = Offset(fieldTopLeft.x, fieldTopLeft.y + stripeHeight * i),
            size = Size(fieldSize.width, stripeHeight)
        )
    }

    drawRoundRect(
        color = ColorGold.copy(alpha = 0.35f),
        topLeft = fieldTopLeft,
        size = fieldSize,
        cornerRadius = CornerRadius(14f, 14f),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawObstacle(rect: Rect) {
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.25f),
        topLeft = Offset(rect.left + 3f, rect.top + 4f),
        size = Size(rect.width, rect.height),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(Color(0xFF7A6A58), Color(0xFF5B4C3D))),
        topLeft = rect.topLeft,
        size = rect.size,
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = ColorGold.copy(alpha = 0.25f),
        topLeft = rect.topLeft,
        size = rect.size,
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawHole(center: Offset) {
    drawCircle(
        color = Color.Black.copy(alpha = 0.25f),
        radius = 26.dp.toPx(),
        center = center
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF0A0A0A), Color(0xFF1B1B1B)),
            center = center,
            radius = 20.dp.toPx()
        ),
        radius = 20.dp.toPx(),
        center = center
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.15f),
        radius = 20.dp.toPx(),
        center = center,
        style = Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawFlag(holeCenter: Offset) {
    val poleHeight = 46.dp.toPx()
    val poleTop = Offset(holeCenter.x, holeCenter.y - poleHeight)

    drawLine(
        color = Color.Black.copy(alpha = 0.2f),
        start = Offset(holeCenter.x + 1.5f, holeCenter.y),
        end = Offset(poleTop.x + 1.5f, poleTop.y),
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = ColorCream,
        start = holeCenter,
        end = poleTop,
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )

    val flagPath = Path().apply {
        moveTo(poleTop.x, poleTop.y)
        lineTo(poleTop.x + 22f, poleTop.y + 7f)
        lineTo(poleTop.x, poleTop.y + 14f)
        close()
    }
    drawPath(path = flagPath, color = ColorGold)
}

private fun DrawScope.drawAimArrow(ball: Offset, directionRad: Float) {
    val arrowLength = 55.dp.toPx()
    val arrowEnd = Offset(
        ball.x + arrowLength * kotlin.math.cos(directionRad),
        ball.y + arrowLength * kotlin.math.sin(directionRad)
    )

    drawLine(
        color = ColorGold.copy(alpha = 0.85f),
        start = ball,
        end = arrowEnd,
        strokeWidth = 3.5f,
        cap = StrokeCap.Round
    )
    drawCircle(color = ColorGold, radius = 4.5f, center = arrowEnd)
}

private fun DrawScope.drawBall(center: Offset) {
    drawCircle(
        color = Color.Black.copy(alpha = 0.25f),
        radius = 12.dp.toPx(),
        center = center + Offset(2.5f, 3f)
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, Color(0xFFDDDDDD)),
            center = center - Offset(4f, 4f),
            radius = 16.dp.toPx()
        ),
        radius = 11.dp.toPx(),
        center = center
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = 3.dp.toPx(),
        center = center - Offset(3.5f, 3.5f)
    )
}