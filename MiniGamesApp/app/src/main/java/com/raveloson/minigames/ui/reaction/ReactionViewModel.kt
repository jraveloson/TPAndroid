package com.raveloson.minigames.ui.reaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

enum class TimerDirection { Up, Down }

private data class RoundConfig(
    val targetTimeMs: Long,
    val startTimerMs: Long,
    val speedMultiplier: Float,
    val direction: TimerDirection
)

private fun randomDirection(): TimerDirection =
    listOf(TimerDirection.Up, TimerDirection.Down).random()

private fun randomSpeedMultiplier(): Float =
    listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f).random()

private fun randomRoundConfig(): RoundConfig {
    val direction = randomDirection()
    val speed = randomSpeedMultiplier()
    val target = Random.nextLong(1000L, 7001L)

    val start = when (direction) {
        TimerDirection.Up -> {
            val maxStart = (target - 100L).coerceAtLeast(0L)
            if (maxStart == 0L) 0L else Random.nextLong(0L, maxStart + 1L)
        }
        TimerDirection.Down -> {
            val minStart = target + 100L
            val maxStart = 9000L
            if (minStart >= maxStart) minStart else Random.nextLong(minStart, maxStart + 1L)
        }
    }

    return RoundConfig(
        targetTimeMs = target,
        startTimerMs = start,
        speedMultiplier = speed,
        direction = direction
    )
}

data class ReactionScreenState(
    val targetTimeMs: Long = 0L,
    val currentTimerMs: Long = 0L,
    val speedMultiplier: Float = 1f,
    val direction: TimerDirection = TimerDirection.Up,
    val isRunning: Boolean = false,
    val showResult: Boolean = false,
    val differenceMs: Long = 0L
)

class ReactionViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReactionScreenState())
    val state: StateFlow<ReactionScreenState> = _state.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    init {
        prepareNewGame()
    }

    private fun prepareNewGame() {
        val config = randomRoundConfig()
        _state.value = _state.value.copy(
            targetTimeMs = config.targetTimeMs,
            currentTimerMs = config.startTimerMs,
            speedMultiplier = config.speedMultiplier,
            direction = config.direction,
            isRunning = false,
            showResult = false,
            differenceMs = 0L
        )
    }

    fun startGame() {
        if (_state.value.isRunning) return

        if (_state.value.showResult) {
            prepareNewGame()
        }
        _state.value = _state.value.copy(
            isRunning = true
        )

        startTimer()
    }

    fun stopTimer() {
        if (!_state.value.isRunning) return

        timerJob?.cancel()
        _state.value = _state.value.copy(
            isRunning = false,
            showResult = true,
            differenceMs = abs(_state.value.currentTimerMs - _state.value.targetTimeMs)
        )
    }

    fun reset() {
        timerJob?.cancel()
        prepareNewGame()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.isRunning) {
                delay(16L)
                val step = (16f * _state.value.speedMultiplier).toLong().coerceAtLeast(1L)
                val newTime = when (_state.value.direction) {
                    TimerDirection.Up -> _state.value.currentTimerMs + step
                    TimerDirection.Down -> (_state.value.currentTimerMs - step).coerceAtLeast(0L)
                }
                _state.value = _state.value.copy(currentTimerMs = newTime)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}