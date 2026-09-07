package com.example.borradordegolf.logic

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Point(val x: Float, val y: Float)

class GolfEngine(
    val holePosition: Point = Point(50f, 10f),
    val startPosition: Point = Point(50f, 90f),
    val holeRadius: Float = 5f
) {
    var ballPosition: Point = startPosition
        private set

    var strokeCount: Int = 0
        private set

    var isHoleCompleted: Boolean = false
        private set

    /**
     * Calcula la nueva posición de la pelota basada en la fuerza y dirección.
     * La dirección se espera en radianes.
     */
    fun hitBall(force: Float, directionRad: Float) {
        if (isHoleCompleted) return

        strokeCount++

        // Cálculo simplificado de movimiento
        // En un swing real, si movemos el celular hacia adelante, la fuerza es la magnitud.
        // La dirección depende de la orientación del dispositivo al momento del impacto.
        val deltaX = force * cos(directionRad)
        val deltaY = force * sin(directionRad)

        val newX = (ballPosition.x + deltaX).coerceIn(0f, 100f)
        val newY = (ballPosition.y + deltaY).coerceIn(0f, 100f)

        ballPosition = Point(newX, newY)

        checkHole()
    }

    private fun checkHole() {
        val dx = ballPosition.x - holePosition.x
        val dy = ballPosition.y - holePosition.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance <= holeRadius) {
            isHoleCompleted = true
        }
    }

    fun reset() {
        ballPosition = startPosition
        strokeCount = 0
        isHoleCompleted = false
    }
}
