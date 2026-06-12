package com.raveloson.minigames.ui.wordgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Phase { PLAYING, GAME_OVER }

data class Cell(
	val char: Char,
	val isSelected: Boolean = false
)

data class WordGameState(
	val phase: Phase = Phase.PLAYING,
	val cells: List<Cell> = emptyList(),
	val selectedLetters: List<Char> = emptyList(),
	val score: Int = 0,
	val remainingSeconds: Int = 60
)

private data class RoundConfig(
	val hiddenWord: String,
	val cells: List<Cell>
)

class WordGameViewModel : ViewModel() {
	private val _state = MutableStateFlow(WordGameState())
	val state: StateFlow<WordGameState> = _state.asStateFlow()

	private var timerJob: Job? = null
	private var hiddenWord: String = ""

	private val wordList = listOf(
		"SOLEIL", "MAISON", "JARDIN", "CHEMIN", "BOUTON",
		"MIROIR", "PLANTE", "CARTON", "FUSEAU", "CITRON",
		"VIOLON", "RAPIDE", "BLOQUE", "MOUTON", "GATEAU"
	)

	fun startGame() {
		if (_state.value.phase == Phase.PLAYING && timerJob?.isActive == true) return

		timerJob?.cancel()
		hiddenWord = ""
		_state.value = WordGameState(
			phase = Phase.PLAYING,
			cells = emptyList(),
			selectedLetters = emptyList(),
			score = 0,
			remainingSeconds = 60
		)

		loadNewRound()
		startTimer()
	}

	fun selectCell(index: Int) {
		val current = _state.value
		if (current.phase != Phase.PLAYING) return
		if (index !in current.cells.indices) return
		if (current.cells[index].isSelected) return
		if (current.selectedLetters.size >= 6) return

		val updatedCells = current.cells.toMutableList().also {
			it[index] = it[index].copy(isSelected = true)
		}

		_state.value = current.copy(
			cells = updatedCells,
			selectedLetters = current.selectedLetters + current.cells[index].char
		)
	}

	fun eraseLast() {
		val current = _state.value
		if (current.phase != Phase.PLAYING) return
		val lastIndex = current.cells.indexOfLast { it.isSelected }
		if (lastIndex == -1) return

		val updatedCells = current.cells.toMutableList().also {
			it[lastIndex] = it[lastIndex].copy(isSelected = false)
		}

		_state.value = current.copy(
			cells = updatedCells,
			selectedLetters = current.selectedLetters.dropLast(1)
		)
	}

	fun validate() {
		val current = _state.value
		if (current.phase != Phase.PLAYING) return
		if (current.selectedLetters.size != 6) return

		val attempt = current.selectedLetters.joinToString("")
		if (attempt == hiddenWord) {
			_state.value = current.copy(score = current.score + 1)
			loadNewRound()
		}
	}

	fun pass() {
		val current = _state.value
		if (current.phase != Phase.PLAYING) return
		loadNewRound()
	}

	fun reset() {
		timerJob?.cancel()
		hiddenWord = ""
		_state.value = WordGameState()
	}

	private fun loadNewRound() {
		val round = createRoundConfig()
		hiddenWord = round.hiddenWord
		_state.value = _state.value.copy(
			cells = round.cells,
			selectedLetters = emptyList()
		)
	}

	private fun createRoundConfig(): RoundConfig {
		val hiddenWord = wordList.random()
		val randomLetters = ('A'..'Z')
			.filterNot { it in hiddenWord }
			.shuffled()
			.take(3)

		val letters = (hiddenWord.toList() + randomLetters).shuffled()
		return RoundConfig(
			hiddenWord = hiddenWord,
			cells = letters.map { Cell(it) }
		)
	}

	private fun startTimer() {
		timerJob?.cancel()
		timerJob = viewModelScope.launch {
			while (_state.value.phase == Phase.PLAYING && _state.value.remainingSeconds > 0) {
				delay(1000L)
				val nextValue = _state.value.remainingSeconds - 1
				if (nextValue <= 0) {
					_state.value = _state.value.copy(
						remainingSeconds = 0,
						phase = Phase.GAME_OVER
					)
					break
				} else {
					_state.value = _state.value.copy(remainingSeconds = nextValue)
				}
			}
		}
	}

	override fun onCleared() {
		super.onCleared()
		timerJob?.cancel()
	}
}