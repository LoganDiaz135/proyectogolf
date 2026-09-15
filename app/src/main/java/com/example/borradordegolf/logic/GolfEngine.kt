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

        val oldX = ballPosition.x
        val oldY = ballPosition.y

        val deltaX = force * cos(directionRad)
        val deltaY = force * sin(directionRad)

        val newX = (ballPosition.x + deltaX).coerceIn(0f, 100f)
        val newY = (ballPosition.y + deltaY).coerceIn(0f, 100f)

        // Comprobamos si la trayectoria pasó por el hoyo antes de actualizar la posición
        checkTrajectory(oldX, oldY, newX, newY)
        
        if (isHoleCompleted) {
            // Si entró en el hoyo por el camino, la pelota se queda en el hoyo
            ballPosition = holePosition
        } else {
            ballPosition = Point(newX, newY)

            checkHole()
            if (isHoleCompleted) {
                ballPosition = holePosition
            }
        }
    }

    private fun checkTrajectory(x1: Float, y1: Float, x2: Float, y2: Float) {
        val hx = holePosition.x
        val hy = holePosition.y

        // Vector del segmento P1 -> P2
        val dx = x2 - x1
        val dy = y2 - y1

        // Si la pelota casi no se movió, dejamos que checkHole se encargue
        if (dx == 0f && dy == 0f) return

        // t es la posición relativa en el segmento (0 a 1)
        val t = ((hx - x1) * dx + (hy - y1) * dy) / (dx * dx + dy * dy)

        // Si la proyección cae dentro del segmento (0 <= t <= 1)
        if (t in 0.0f..1.0f) {
            val closestX = x1 + t * dx
            val closestY = y1 + t * dy
            val distDx = hx - closestX
            val distDy = hy - closestY
            val distance = sqrt(distDx * distDx + distDy * distDy)

            if (distance <= holeRadius) {
                isHoleCompleted = true
            }
        }
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
