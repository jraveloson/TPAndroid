package com.raveloson.minigames.ui.reaction

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@SuppressLint("DefaultLocale")
@Composable
fun ReactionScreen(onBackClick: () -> Unit) {
    var targetTimeMs by remember { mutableLongStateOf(0L) }
    var startTimerMs by remember { mutableLongStateOf(0L) }
    var speedMultiplier by remember { mutableFloatStateOf(1f) }
    var direction by remember { mutableStateOf(TimerDirection.Up) }
    var currentTimerMs by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

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
        else -> "Continue à t'entraner !"
    }

    val targetText = String.format("%.3f s", targetTimeMs / 1000f)
    val currentText = String.format("%.3f s", currentTimerMs / 1000f)
    val diffText = String.format("%.3f s", differenceMs / 1000f)
    val speedText = String.format("x%.2f", speedMultiplier)
    val directionText = if (direction == TimerDirection.Up) "+" else "-"

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFAAFFAF))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Titre en haut (sans trop de padding)
        Text(
            text = "Timer Game",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            textAlign = TextAlign.Center
        )

        // Carte principale
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Temps cible
                Text(
                    text = "Temps cible",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = targetText,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Infos vitesse et direction
                Text(
                    text = "Vitesse: $speedText  |  Sens: $directionText",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Séparateur visuel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Votre temps
                Text(
                    text = "Votre temps",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = currentText,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRunning) MaterialTheme.colorScheme.secondary else Color.Gray
                )

                // Résultat si manche terminée
                if (showResult) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ecart",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = diffText,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            differenceMs < 100L -> Color(0xFF4CAF50) // Vert
                            differenceMs < 300L -> Color(0xFFFBC02D) // Jaune
                            else -> Color(0xFFFF9800)                 // Orange
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = feedback,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Boutons en bas
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    if (!isRunning) {
                        if (showResult) {
                            prepareNewRound()
                        }
                        currentTimerMs = startTimerMs
                        isRunning = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Lancer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Button(
                onClick = {
                    if (isRunning) {
                        isRunning = false
                        showResult = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    "Arreter",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(8.dp)
                )
            }

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Retour",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}