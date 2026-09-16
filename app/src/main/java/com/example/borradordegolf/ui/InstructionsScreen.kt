package com.example.borradordegolf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InstructionsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorForestDark)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = onBack) {
                Text("‹ Menú", color = ColorCream, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "CÓMO JUGAR",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = ColorCream
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .height(3.dp)
                .background(ColorGold)
        )

        Spacer(modifier = Modifier.height(28.dp))

        StepCard(
            number = 1,
            title = "Sostén el celular",
            body = "Agárralo con firmeza, como si fuera el palo de golf."
        )
        StepCard(
            number = 2,
            title = "Apunta",
            body = "Gira el celular sobre su propio plano, como un volante. La flecha dorada que sale de la bola te muestra hacia dónde va a salir."
        )
        StepCard(
            number = 3,
            title = "Golpea",
            body = "Haz un swing rápido con el brazo, como si golpearas la bola de verdad. Mientras más rápido el movimiento, más fuerte el golpe."
        )
        StepCard(
            number = 4,
            title = "Mete la bola",
            body = "Lleva la bola al hoyo con la menor cantidad de golpes posible. El «Par» te dice cuántos se esperan en ese hoyo."
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Si tu celular no tiene giroscopio, la puntería no se podrá mover.",
            fontSize = 12.sp,
            color = ColorCream.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorGold,
                contentColor = ColorForestDark
            )
        ) {
            Text("ENTENDIDO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun StepCard(number: Int, title: String, body: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorPanelDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(ColorGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorForestDark
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = body,
                    fontSize = 13.sp,
                    color = ColorCream.copy(alpha = 0.85f)
                )
            }
        }
    }
}