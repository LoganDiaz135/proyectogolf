package com.example.borradordegolf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.borradordegolf.logic.LEVELS

@Composable
fun LevelSelectScreen(
    unlockedLevels: Set<Int>,
    completedLevels: Set<Int>,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorForestDark)
            .padding(24.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("‹ Menú", color = ColorCream, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SELECCIONA NIVEL",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = ColorCream
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LEVELS.forEach { level ->
                LevelCard(
                    number = level.number,
                    isUnlocked = level.number in unlockedLevels,
                    isCompleted = level.number in completedLevels,
                    onClick = { onLevelSelected(level.number) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LevelCard(
    number: Int,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isCompleted -> ColorGold
        isUnlocked -> ColorPanelDark
        else -> ColorPanelDark.copy(alpha = 0.4f)
    }
    val contentColor = if (isCompleted) ColorForestDark else ColorCream

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(enabled = isUnlocked) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                isCompleted -> "✓"
                isUnlocked -> ""
                else -> "🔒"
            },
            fontSize = 22.sp,
            color = if (isCompleted) ColorForestDark else ColorGold
        )
        Text(
            text = number.toString(),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor.copy(alpha = if (isUnlocked) 1f else 0.5f)
        )
    }
}