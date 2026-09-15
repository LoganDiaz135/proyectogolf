package com.example.borradordegolf.logic

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val FIELD_MIN = 0f
private const val FIELD_MAX = 100f
private const val MAX_BOUNCES = 8
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

        var x = ballPosition.x
        var y = ballPosition.y
        var dx = force * cos(directionRad)
        var dy = force * sin(directionRad)

        var bounces = 0
        while (bounces <= MAX_BOUNCES) {
            val endX = x + dx
            val endY = y + dy

            // ¿En qué punto del recorrido toca cada pared? (2 = no la toca)
            val tX = when {
                endX < FIELD_MIN -> (FIELD_MIN - x) / dx
                endX > FIELD_MAX -> (FIELD_MAX - x) / dx
                else -> 2f
            }
            val tY = when {
                endY < FIELD_MIN -> (FIELD_MIN - y) / dy
                endY > FIELD_MAX -> (FIELD_MAX - y) / dy
                else -> 2f
            }

            val t = minOf(tX, tY)

            // No choca con nada: tramo final
            if (t > 1f) {
                checkTrajectory(x, y, endX, endY)
                x = endX
                y = endY
                break
            }

            // Choca: avanzamos solo hasta la pared
            val wallX = x + dx * t
            val wallY = y + dy * t
            checkTrajectory(x, y, wallX, wallY)

            x = wallX
            y = wallY

            if (isHoleCompleted) break

            // Rebote: se invierte el eje que chocó
            if (tX <= tY) dx = -dx else dy = -dy

            // Y sigue con lo que le quedaba de impulso
            dx *= (1f - t)
            dy *= (1f - t)

            bounces++
        }

        if (isHoleCompleted) {
            ballPosition = holePosition
        } else {
            ballPosition = Point(
                x.coerceIn(FIELD_MIN, FIELD_MAX),
                y.coerceIn(FIELD_MIN, FIELD_MAX)
            )
            checkHole()
            if (isHoleCompleted) ballPosition = holePosition
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
