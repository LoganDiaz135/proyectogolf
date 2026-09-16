package com.example.borradordegolf.logic

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val FIELD_MIN = 0f
private const val FIELD_MAX = 100f

data class Point(val x: Float, val y: Float)

class GolfEngine(
    val holePosition: Point = Point(50f, 10f),
    val startPosition: Point = Point(50f, 90f),
    val holeRadius: Float = 5f
) {
    var ballPosition: Point = startPosition
        private set

    private var velocityX: Float = 0f
    private var velocityY: Float = 0f

    var isMoving: Boolean = false
        private set

    var strokeCount: Int = 0
        private set

    var isHoleCompleted: Boolean = false
        private set

    companion object {
        // Fracción de velocidad que se pierde por segundo debido a la fricción del pasto
        const val FRICTION_PER_SECOND = 0.5f
        // Velocidad por debajo de la cual consideramos que la pelota está detenida
        const val MIN_SPEED = 1.5f
        // Fracción de velocidad conservada al rebotar contra una pared
        const val WALL_RESTITUTION = 0.65f
    }

    /**
     * Establece la velocidad inicial de la pelota. No mueve la pelota directamente:
     * el movimiento real ocurre llamando a step() repetidamente.
     */
    fun hitBall(force: Float, directionRad: Float) {
        if (isHoleCompleted || isMoving) return

        strokeCount++
        velocityX = force * cos(directionRad)
        velocityY = force * sin(directionRad)
        isMoving = true
    }

    /**
     * Avanza la simulación un pequeño intervalo de tiempo (en segundos).
     * Debe llamarse repetidamente (ej. cada 16ms) mientras isMoving == true.
     */
    fun step(deltaTime: Float) {
        if (!isMoving || isHoleCompleted) return

        val prevX = ballPosition.x
        val prevY = ballPosition.y

        var newX = prevX + velocityX * deltaTime
        var newY = prevY + velocityY * deltaTime

        // Rebote contra las paredes exteriores en X
        if (newX < FIELD_MIN) {
            newX = FIELD_MIN
            velocityX = -velocityX * WALL_RESTITUTION
        } else if (newX > FIELD_MAX) {
            newX = FIELD_MAX
            velocityX = -velocityX * WALL_RESTITUTION
        }

        // Rebote contra las paredes exteriores en Y
        if (newY < FIELD_MIN) {
            newY = FIELD_MIN
            velocityY = -velocityY * WALL_RESTITUTION
        } else if (newY > FIELD_MAX) {
            newY = FIELD_MAX
            velocityY = -velocityY * WALL_RESTITUTION
        }

        // Revisamos si en este tramo del recorrido la pelota pasó por el hoyo
        checkTrajectory(prevX, prevY, newX, newY)

        if (isHoleCompleted) {
            ballPosition = holePosition
            velocityX = 0f
            velocityY = 0f
            isMoving = false
            return
        }

        ballPosition = Point(newX, newY)

        // Fricción: reduce la velocidad de forma exponencial, independiente del framerate
        val frictionFactor = (1.0 - FRICTION_PER_SECOND.toDouble())
            .pow(deltaTime.toDouble())
            .toFloat()
        velocityX *= frictionFactor
        velocityY *= frictionFactor

        val speed = sqrt(velocityX * velocityX + velocityY * velocityY)
        if (speed < MIN_SPEED) {
            velocityX = 0f
            velocityY = 0f
            isMoving = false
            checkHole()
        }
    }

    private fun checkTrajectory(x1: Float, y1: Float, x2: Float, y2: Float) {
        val hx = holePosition.x
        val hy = holePosition.y

        val dx = x2 - x1
        val dy = y2 - y1

        if (dx == 0f && dy == 0f) return

        val t = ((hx - x1) * dx + (hy - y1) * dy) / (dx * dx + dy * dy)

        if (t in 0f..1f) {
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
        velocityX = 0f
        velocityY = 0f
        isMoving = false
        strokeCount = 0
        isHoleCompleted = false
    }
}