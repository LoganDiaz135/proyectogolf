package com.example.borradordegolf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.borradordegolf.logic.GolfEngine
import com.example.borradordegolf.logic.LEVELS
import com.example.borradordegolf.logic.Obstacle
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
    val aimingDirectionRad: Float = -Math.PI.toFloat() / 2f,
    val currentLevelNumber: Int = 1,
    val par: Int = LEVELS.first().par,
    val obstacles: List<Obstacle> = emptyList()
)

class GolfViewModel : ViewModel() {
    private var engine = GolfEngine(
        holePosition = LEVELS.first().hole,
        startPosition = LEVELS.first().start,
        holeRadius = LEVELS.first().holeRadius,
        obstacles = LEVELS.first().obstacles
    )

    private var currentLevelIndex = 0

    private val _uiState = MutableStateFlow(GolfState())
    val uiState: StateFlow<GolfState> = _uiState.asStateFlow()

    private val _unlockedLevels = MutableStateFlow(setOf(1))
    val unlockedLevels: StateFlow<Set<Int>> = _unlockedLevels.asStateFlow()

    private val _completedLevels = MutableStateFlow(emptySet<Int>())
    val completedLevels: StateFlow<Set<Int>> = _completedLevels.asStateFlow()

    private var simulationJob: Job? = null

    private val SIMULATION_STEP_MS = 16L
    private val SIMULATION_STEP_SECONDS = SIMULATION_STEP_MS / 1000f

    init {
        updateState(0f)
    }

    fun onAimChanged(directionRad: Float) {
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
            updateState(_uiState.value.lastSwingForce)
            handleLevelCompletionIfNeeded()
        }
    }

    private fun handleLevelCompletionIfNeeded() {
        if (!engine.isHoleCompleted) return
        val levelNumber = currentLevelIndex + 1
        _completedLevels.update { it + levelNumber }
        if (levelNumber < LEVELS.size) {
            _unlockedLevels.update { it + (levelNumber + 1) }
        }
    }

    /** Carga un nivel específico. No hace nada si el nivel está bloqueado. */
    fun loadLevel(levelNumber: Int) {
        if (levelNumber !in _unlockedLevels.value) return

        simulationJob?.cancel()
        currentLevelIndex = levelNumber - 1
        val level = LEVELS[currentLevelIndex]

        engine = GolfEngine(
            holePosition = level.hole,
            startPosition = level.start,
            holeRadius = level.holeRadius,
            obstacles = level.obstacles
        )
        updateState(0f)
    }

    /** Usado por el botón "JUGAR": continúa desde el primer nivel no completado. */
    fun startFromProgress() {
        val nextLevel = (1..LEVELS.size).firstOrNull { it !in _completedLevels.value }
            ?: LEVELS.size
        loadLevel(nextLevel)
    }

    fun resetGame() {
        simulationJob?.cancel()
        engine.reset()
        updateState(0f)
    }

    private fun updateState(lastForce: Float) {
        val level = LEVELS[currentLevelIndex]
        _uiState.update {
            it.copy(
                ballPosition = engine.ballPosition,
                holePosition = engine.holePosition,
                strokeCount = engine.strokeCount,
                isHoleCompleted = engine.isHoleCompleted,
                isBallMoving = engine.isMoving,
                lastSwingForce = lastForce,
                currentLevelNumber = level.number,
                par = level.par,
                obstacles = level.obstacles
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}