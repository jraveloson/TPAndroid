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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private fun randomTargetTime(): Long {
    return (1000L..7000L).random()
}

enum class GamePhase {
    Ready,
    Running,
    Result
}

@SuppressLint("DefaultLocale")
@Composable
fun ReactionScreen(onBackClick: () -> Unit) {
    var phase by remember { mutableStateOf(GamePhase.Ready) }
    var targetTimeMs by remember { mutableLongStateOf(randomTargetTime()) }
    var nextTargetTimeMs by remember { mutableLongStateOf(randomTargetTime()) }
    var isRunning by remember { mutableStateOf(false) }
    var elapsedTimeMs by remember { mutableLongStateOf(0L) }
    var showResult by remember { mutableStateOf(false) }

    val differenceMs = kotlin.math.abs(elapsedTimeMs - targetTimeMs)
    val feedback = when {
        differenceMs < 100 -> "Excellent !"
        differenceMs < 300 -> "Très bien !"
        differenceMs < 600 -> "Pas mal !"
        else -> "Continue à t'entraîner !"
    }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                delay(16L)
                elapsedTimeMs += 16L
            }
        }
    }

    val currentTime = String.format("%.3f s", elapsedTimeMs / 1000f)
    val targetTime = String.format("%.3f s", targetTimeMs / 1000f)
    val differenceText = String.format("%.3f s", differenceMs / 1000f)

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
                text = "Mini-jeu de réaction",
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
                        text = targetTime,
                        fontSize = 32.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Votre temps")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentTime,
                        fontSize = 32.sp
                    )

                    if (showResult) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(text = "Écart")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = differenceText,
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
                    when(phase) {
                        GamePhase.Ready -> {
                            elapsedTimeMs = 0L
                            showResult = false
                            isRunning = true
                            phase = GamePhase.Running
                        }
                        GamePhase.Result -> {
                            targetTimeMs = randomTargetTime()
                            elapsedTimeMs = 0L
                            showResult = false
                            isRunning = true
                            phase = GamePhase.Running
                        }
                        GamePhase.Running -> Unit
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lancer")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (phase == GamePhase.Running) {
                        isRunning = false
                        showResult = true
                        phase = GamePhase.Result
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Arrêter")
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