package com.example.borradordegolf

import com.example.borradordegolf.logic.GolfEngine
import com.example.borradordegolf.logic.Point
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GolfEngineTest {

    @Test
    fun `hitBall increases stroke count`() {
        val engine = GolfEngine()
        engine.hitBall(10f, 0f)
        assertEquals(1, engine.strokeCount)
    }

    @Test
    fun `hitBall moves ball position correctly`() {
        val startPos = Point(50f, 50f)
        val engine = GolfEngine(startPosition = startPos)
        
        // Mover 10 unidades a la derecha (0 radianes)
        engine.hitBall(10f, 0f)
        
        assertEquals(60f, engine.ballPosition.x, 0.01f)
        assertEquals(50f, engine.ballPosition.y, 0.01f)
    }

    @Test
    fun `checkHole detects completion when ball is close`() {
        val holePos = Point(50f, 10f)
        val engine = GolfEngine(holePosition = holePos, holeRadius = 5f)
        
        // Pelota inicia en 50, 90. Moverla cerca del hoyo.
        // Mover 80 unidades hacia arriba (-PI/2 radianes)
        engine.hitBall(80f, -Math.PI.toFloat() / 2f)
        
        assertTrue("El hoyo debería estar completado", engine.isHoleCompleted)
    }

    @Test
    fun `reset sets everything to initial state`() {
        val engine = GolfEngine()
        engine.hitBall(10f, 0f)
        engine.reset()
        
        assertEquals(0, engine.strokeCount)
        assertEquals(engine.startPosition, engine.ballPosition)
        assertEquals(false, engine.isHoleCompleted)
    }
}
