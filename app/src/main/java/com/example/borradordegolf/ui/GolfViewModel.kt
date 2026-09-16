package com.example.borradordegolf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.borradordegolf.logic.GolfEngine
import com.example.borradordegolf.logic.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GolfState(
    val ballPosition: Point = Point(50f, 90f),
    val holePosition: Point = Point(50f, 10f),
    val strokeCount: Int = 0,
    val isHoleCompleted: Boolean = false,
    val isBallMoving: Boolean = false,
    val lastSwingForce: Float = 0f,
    val aimingDirectionRad: Float = -Math.PI.toFloat() / 2f
)

class GolfViewModel : ViewModel() {
    private val engine = GolfEngine()

    private val _uiState = MutableStateFlow(GolfState())
    val uiState: StateFlow<GolfState> = _uiState.asStateFlow()

    private var simulationJob: Job? = null

    // Aproximadamente 60 pasos de simulación por segundo
    private val SIMULATION_STEP_MS = 16L
    private val SIMULATION_STEP_SECONDS = SIMULATION_STEP_MS / 1000f

    fun onAimChanged(directionRad: Float) {
        // No se permite cambiar la dirección mientras la pelota está en movimiento
        if (engine.isMoving || engine.isHoleCompleted) return
        _uiState.update { it.copy(aimingDirectionRad = directionRad) }
    }

    fun onSwingDetected(force: Float) {
        if (engine.isMoving || engine.isHoleCompleted) return

        val currentAim = _uiState.value.aimingDirectionRad
        engine.hitBall(force, currentAim)
        updateState(force)
        startSimulationLoop()
    }

    private fun startSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            while (engine.isMoving) {
                engine.step(SIMULATION_STEP_SECONDS)
                updateState(_uiState.value.lastSwingForce)
                delay(SIMULATION_STEP_MS)
            }
            // Aseguramos que el estado final (detenida o con hoyo completado) quede reflejado
            updateState(_uiState.value.lastSwingForce)
        }
    }

    fun resetGame() {
        simulationJob?.cancel()
        engine.reset()
        updateState(0f)
    }

    private fun updateState(lastForce: Float) {
        _uiState.update {
            it.copy(
                ballPosition = engine.ballPosition,
                strokeCount = engine.strokeCount,
                isHoleCompleted = engine.isHoleCompleted,
                isBallMoving = engine.isMoving,
                lastSwingForce = lastForce
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}