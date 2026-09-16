package com.example.borradordegolf

import com.example.borradordegolf.logic.GolfEngine
import com.example.borradordegolf.logic.Obstacle
import com.example.borradordegolf.logic.Point
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI
import kotlin.math.sqrt

class GolfEngineTest {

    @Test
    fun `verifyForceCalculation - The hit must apply the initial velocity proportional to the force`() {
        val engine = GolfEngine(startPosition = Point(50f, 50f))
        
        // Golpeamos con una fuerza de 50f hacia la derecha (0 radianes)
        engine.hitBall(force = 50f, directionRad = 0f)
        
        assertTrue("La pelota debería entrar en estado de movimiento", engine.isMoving)
        
        // Avanzamos un paso de simulación corto para ver la nueva posición
        engine.step(0.1f)
        
        // Con dirección 0 radianes, cos(0)=1 y sin(0)=0. La pelota se desplaza en X pero no en Y.
        assertTrue("La posición en X debió aumentar", engine.ballPosition.x > 50f)
        assertEquals("La posición en Y debió mantenerse igual", 50f, engine.ballPosition.y, 0.01f)
    }

    @Test
    fun `verifyShotDirection - The ball must move in the direction of the provided angle`() {
        // Caso 1: Dirección hacia ARRIBA (-PI / 2 radianes)
        val upEngine = GolfEngine(startPosition = Point(50f, 50f))
        upEngine.hitBall(force = 20f, directionRad = -PI.toFloat() / 2f)
        upEngine.step(0.1f)
        assertTrue("Hacia arriba: Y debe disminuir", upEngine.ballPosition.y < 50f)
        assertEquals("Hacia arriba: X debe mantenerse igual", 50f, upEngine.ballPosition.x, 0.01f)

        // Caso 2: Dirección hacia ABAJO (PI / 2 radianes)
        val downEngine = GolfEngine(startPosition = Point(50f, 50f))
        downEngine.hitBall(force = 20f, directionRad = PI.toFloat() / 2f)
        downEngine.step(0.1f)
        assertTrue("Hacia abajo: Y debe aumentar", downEngine.ballPosition.y > 50f)
        assertEquals("Hacia abajo: X debe mantenerse igual", 50f, downEngine.ballPosition.x, 0.01f)

        // Caso 3: Dirección hacia la IZQUIERDA (PI radianes)
        val leftEngine = GolfEngine(startPosition = Point(50f, 50f))
        leftEngine.hitBall(force = 20f, directionRad = PI.toFloat())
        leftEngine.step(0.1f)
        assertTrue("Hacia la izquierda: X debe disminuir", leftEngine.ballPosition.x < 50f)
        assertEquals("Hacia la izquierda: Y debe mantenerse igual", 50f, leftEngine.ballPosition.y, 0.01f)
    }

    @Test
    fun `verifyHoleValidation - It must detect that the ball enters the hole`() {
        val hole = Point(50f, 10f)
        val holeRadius = 5f
        // Colocamos la pelota cerca del hoyo
        val engine = GolfEngine(holePosition = hole, startPosition = Point(50f, 12f), holeRadius = holeRadius)
        
        // Golpeamos hacia el hoyo
        engine.hitBall(force = 10f, directionRad = -PI.toFloat() / 2f)
        
        // Simulamos los pasos hasta que se complete el hoyo
        var steps = 0
        while (engine.isMoving && !engine.isHoleCompleted && steps < 200) {
            engine.step(0.016f)
            steps++
        }
        
        assertTrue("El sistema debe validar automáticamente que alcanzó el hoyo", engine.isHoleCompleted)
        
        val dx = engine.ballPosition.x - hole.x
        val dy = engine.ballPosition.y - hole.y
        val dist = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        assertTrue("La pelota debe terminar dentro o sobre el hoyo", dist <= holeRadius)
    }

    @Test
    fun `verifyObstacleCollision - The ball must bounce when colliding with an obstacle`() {
        // Ponemos un obstáculo justo a la derecha de la pelota
        val obstacle = Obstacle(left = 60f, top = 40f, right = 70f, bottom = 60f)
        val engine = GolfEngine(startPosition = Point(55f, 50f), obstacles = listOf(obstacle))
        
        // Golpeamos hacia la derecha (hacia el obstáculo)
        engine.hitBall(force = 100f, directionRad = 0f)
        
        // Simulamos unos pasos de tiempo
        repeat(20) {
            engine.step(0.016f)
        }
        
        // Al rebotar contra el obstáculo en X = 60, la pelota no debe cruzar el límite de 60f
        assertTrue("La pelota no debe atravesar el obstáculo (X <= 60)", engine.ballPosition.x <= 60f)
    }

    @Test
    fun `verifyStrokeCounter - Each valid stroke must increment the counter`() {
        val engine = GolfEngine()
        assertEquals(0, engine.strokeCount)
        
        engine.hitBall(10f, 0f)
        assertEquals(1, engine.strokeCount)
        
        // Forzamos detención
        var loops = 0
        while (engine.isMoving && loops < 1000) {
            engine.step(0.1f)
            loops++
        }
        
        engine.hitBall(15f, 0f)
        assertEquals(2, engine.strokeCount)
    }

    @Test
    fun `verifyGameReset - On reset everything returns to its initial state`() {
        val start = Point(50f, 90f)
        val engine = GolfEngine(startPosition = start)
        
        engine.hitBall(30f, -PI.toFloat() / 2f)
        engine.step(0.1f)
        
        assertTrue(engine.strokeCount > 0)
        
        engine.reset()
        
        assertEquals("El contador de golpes debe restablecerse a 0", 0, engine.strokeCount)
        assertEquals("La pelota debe volver a su posición inicial", start, engine.ballPosition)
        assertFalse("La pelota no debe estar moviéndose", engine.isMoving)
        assertFalse("El hoyo no debe estar completado", engine.isHoleCompleted)
    }
}
