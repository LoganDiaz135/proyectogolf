package com.example.borradordegolf.ui

import androidx.lifecycle.ViewModel
import com.example.borradordegolf.logic.GolfEngine
import com.example.borradordegolf.logic.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GolfState(
    val ballPosition: Point = Point(50f, 90f),
    val holePosition: Point = Point(50f, 10f),
    val strokeCount: Int = 0,
    val isHoleCompleted: Boolean = false,
    val lastSwingForce: Float = 0f,
    val aimingDirectionRad: Float = -Math.PI.toFloat() / 2f
)

class GolfViewModel : ViewModel() {
    private val engine = GolfEngine()
    
    private val _uiState = MutableStateFlow(GolfState())
    val uiState: StateFlow<GolfState> = _uiState.asStateFlow()

    fun onAimChanged(directionRad: Float) {
        _uiState.update { it.copy(aimingDirectionRad = directionRad) }
    }

    fun onSwingDetected(force: Float) {
        // Usamos la dirección actual apuntada con el giroscopio
        val currentAim = _uiState.value.aimingDirectionRad
        engine.hitBall(force, currentAim)
        updateState(force)
    }

    fun resetGame() {
        engine.reset()
        updateState(0f)
    }

    private fun updateState(lastForce: Float) {
        _uiState.update {
            it.copy(
                ballPosition = engine.ballPosition,
                strokeCount = engine.strokeCount,
                isHoleCompleted = engine.isHoleCompleted,
                lastSwingForce = lastForce
            )
        }
    }
}
