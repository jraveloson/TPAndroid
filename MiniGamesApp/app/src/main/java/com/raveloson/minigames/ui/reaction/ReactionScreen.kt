
package com.raveloson.minigames.ui.reaction

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

private enum class TimerDirection { Up, Down }

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

    // Cible dans une plage lisible
    val target = Random.nextLong(1000L, 7001L)

    // Cohérence demandée :
    // Up   -> start < target
    // Down -> start > target
    val start = when (direction) {
        TimerDirection.Up -> {
            // Au moins 100 ms d'écart pour éviter start == target
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

@SuppressLint("DefaultLocale")
@Composable
fun ReactionScreen(onBackClick: () -> Unit) {
    // Manche active
    var targetTimeMs by remember { mutableLongStateOf(0L) }
    var startTimerMs by remember { mutableLongStateOf(0L) }
    var speedMultiplier by remember { mutableFloatStateOf(1f) }
    var direction by remember { mutableStateOf(TimerDirection.Up) }

    // Timer courant (toujours initialisé via startTimerMs)
    var currentTimerMs by remember { mutableLongStateOf(0L) }

    var isRunning by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    // Init 1ère manche
    LaunchedEffect(Unit) {
        val first = randomRoundConfig()
        targetTimeMs = first.targetTimeMs
        startTimerMs = first.startTimerMs
        speedMultiplier = first.speedMultiplier
        direction = first.direction
        currentTimerMs = first.startTimerMs
    }

    fun prepareNewRound() {
        val next = randomRoundConfig()
        targetTimeMs = next.targetTimeMs
        startTimerMs = next.startTimerMs
        speedMultiplier = next.speedMultiplier
        direction = next.direction
        currentTimerMs = next.startTimerMs
        showResult = false
    }

    LaunchedEffect(isRunning, direction, speedMultiplier) {
        if (isRunning) {
            while (isRunning) {
                delay(16L)
                val step = (16f * speedMultiplier).toLong().coerceAtLeast(1L)
                currentTimerMs = when (direction) {
                    TimerDirection.Up -> currentTimerMs + step
                    TimerDirection.Down -> (currentTimerMs - step).coerceAtLeast(0L)
                }
            }
        }
    }

    val differenceMs = abs(currentTimerMs - targetTimeMs)
    val feedback = when {
        differenceMs < 100L -> "Excellent !"
        differenceMs < 300L -> "Tres bien !"
        differenceMs < 600L -> "Pas mal !"
        else -> "Continue a t'entrainer !"
    }

    val targetText = String.format("%.3f s", targetTimeMs / 1000f)
    val currentText = String.format("%.3f s", currentTimerMs / 1000f)
    val diffText = String.format("%.3f s", differenceMs / 1000f)
    val speedText = String.format("x%.2f", speedMultiplier)
    val directionText = if (direction == TimerDirection.Up) "Croissant (Up)" else "Decroissant (Down)"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mini-jeu de reaction",
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Temps cible")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = targetText,
                        fontSize = 32.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Vitesse: $speedText")
                    Text(text = "Sens: $directionText")

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Votre temps")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentText,
                        fontSize = 32.sp
                    )

                    if (showResult) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "Ecart")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = diffText,
                            fontSize = 24.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = feedback)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (!isRunning) {
                        // Si on vient d'un resultat, on prepare une nouvelle manche complete
                        if (showResult) {
                            prepareNewRound()
                        }
                        // Au lancement, on repart de la valeur de depart de la manche
                        currentTimerMs = startTimerMs
                        isRunning = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lancer")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (isRunning) {
                        isRunning = false
                        showResult = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Arreter")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retour")
            }
        }
    }
}