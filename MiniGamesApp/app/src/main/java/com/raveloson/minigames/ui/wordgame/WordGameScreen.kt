package com.raveloson.minigames.ui.wordgame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Locale

@Composable
fun WordGameScreen(
	onBackClick: () -> Unit,
	viewModel: WordGameViewModel = viewModel()
) {
	val state by viewModel.state.collectAsState()

	LaunchedEffect(Unit) {
		viewModel.startGame()
	}

	val timerText = String.format(Locale.getDefault(), "%02d", state.remainingSeconds)
	val selectedText = state.selectedLetters.joinToString("")

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(Color(0xFFAAFFAF))
			.padding(20.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text(
			text = "Mot caché",
			textAlign = TextAlign.Center,
			fontSize = 32.sp,
			fontWeight = FontWeight.Bold,
			color = MaterialTheme.colorScheme.primary,
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 20.dp)
		)

		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(containerColor = Color(0xFFAAFFAF)),
			elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
		) {
			Column(
				modifier = Modifier.padding(20.dp),
				verticalArrangement = Arrangement.spacedBy(12.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = if (state.phase == Phase.GAME_OVER) "Temps écoulé" else "Temps restant",
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
				)
				Text(
					text = timerText,
					fontSize = 42.sp,
					fontWeight = FontWeight.Bold,
					color = MaterialTheme.colorScheme.secondary
				)

				Text(
					text = "Score : ${state.score}",
					fontSize = 18.sp,
					fontWeight = FontWeight.SemiBold,
					color = Color(0xFFFBC02D)
				)

				Text(
					text = if (state.phase == Phase.GAME_OVER) {
						"Partie terminée"
					} else {
						"Mot de 6 lettres"
					},
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
				)
			}
		}

		Card(
			modifier = Modifier.fillMaxWidth(),
			colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
			elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
		) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = selectedText.ifEmpty { "___" },
					modifier = Modifier.weight(1f),
					fontSize = 24.sp,
					fontWeight = FontWeight.Bold,
					color = if (selectedText.isEmpty()) Color.Gray else Color(0xFF4CAF50)
				)
				IconButton(onClick = { viewModel.eraseLast() }) {
					Text(
						text = "⌫",
						fontSize = 22.sp,
						color = Color(0xFFFBC02D)
					)
				}
			}
		}

		if (state.phase == Phase.PLAYING) {
			Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
				repeat(3) { row ->
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(10.dp)
					) {
						repeat(3) { col ->
							val index = row * 3 + col
							val cell = state.cells.getOrNull(index)
							if (cell != null) {
								val enabled = !cell.isSelected
								Button(
									onClick = { viewModel.selectCell(index) },
									enabled = enabled,
									modifier = Modifier
										.weight(1f)
										.size(84.dp),
									shape = RoundedCornerShape(14.dp),
									colors = ButtonDefaults.buttonColors(
										containerColor = if (cell.isSelected) Color(0xFF2A2A2A) else Color(0xFF1B5E20),
										disabledContainerColor = Color(0xFF2A2A2A),
										contentColor = Color.White,
										disabledContentColor = Color.Gray
									)
								) {
									Text(text = cell.char.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold)
								}
							} else {
								Spacer(modifier = Modifier.weight(1f))
							}
						}
					}
				}

				Button(
					onClick = { viewModel.validate() },
					modifier = Modifier.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
				) {
					Text("Valider", fontWeight = FontWeight.Bold)
				}

				Button(
					onClick = { viewModel.pass() },
					modifier = Modifier.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBC02D), contentColor = Color.Black)
				) {
					Text("Passer", fontWeight = FontWeight.Bold)
				}
			}
		} else {
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
				elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
			) {
				Column(
					modifier = Modifier.padding(20.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Text(
						text = "Score final : ${state.score}",
						fontSize = 20.sp,
						fontWeight = FontWeight.Bold,
						color = Color.White
					)

					Button(
						onClick = { viewModel.reset(); viewModel.startGame() },
						modifier = Modifier.fillMaxWidth(),
						colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
					) {
						Text("Rejouer")
					}

					OutlinedButton(
						onClick = onBackClick,
						modifier = Modifier.fillMaxWidth()
					) {
						Text("Retour")
					}
				}
			}
		}

		if (state.phase == Phase.PLAYING) {
			OutlinedButton(
				onClick = onBackClick,
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Retour")
			}
		}
	}
}