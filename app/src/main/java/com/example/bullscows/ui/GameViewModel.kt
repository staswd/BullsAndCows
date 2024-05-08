package com.example.bullscows.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    lateinit var currentHiddenNumber: String
    private var timerJob: Job? = null

    fun getRandomNumber(): String {
        return (0..9).shuffled().take(4).joinToString("")
    }

    private fun checkUserGuess(guessNumber: String, currentNumber: String) {
        val allGuesses = uiState.value.guesses
        val remainingGuesses = uiState.value.remainingGuesses
        var A = 0
        var B = 0
        if (remainingGuesses > 1) {
            guessNumber.forEachIndexed() { i: Int, c: Char ->
                if (currentNumber.contains(c) && i == currentNumber.indexOf(c)) {
                    A++
                } else if (currentNumber.contains(c)) {
                    B++
                }
            }
            allGuesses[guessNumber] = "$A" + "A" + "$B" + "B"
            _uiState.update { currentState ->
                currentState.copy(
                    guesses = allGuesses,
                    currentUserGuessNumber = "____",
                    remainingGuesses = remainingGuesses - 1
                )
            }
        } else endGame(false)
        if (guessNumber == currentHiddenNumber) endGame(true)
    }

    fun allSymbolFalse() {
        val numbers = uiState.value.numbers
        for ((key) in numbers) {
            numbers[key] = false
        }
        _uiState.update { currentState ->
            currentState.copy(
                numbers = numbers,
            )
        }
    }

    fun toggleSymbolState(s: String) {
        val numbers = uiState.value.numbers
        for ((key, value) in numbers) {
            if (key == s) {
                numbers[key] = !value
            }
        }
        _uiState.update { currentState ->
            currentState.copy(
                numbers = numbers,
            )
        }
    }

    private fun score(steps: Int, seconds: Int): String {
        return (5000 - steps * seconds).toString()
    }

    private fun endGame(win: Boolean) {
        val score = score(8 - uiState.value.remainingGuesses, uiState.value.elapsedTime)
        _uiState.update { currentState ->
            currentState.copy(
                gameOver = true,
                gameWin = win,
                isRunning = false,
                finalScore = score
            )
        }
        timerJob?.cancel()
    }

    fun deleteLastSymbolUserGuess() {
        var currentUserGuessNumber: String =
            uiState.value.currentUserGuessNumber.filterNot { it == '_' }
        if (currentUserGuessNumber.isNotEmpty()) {
            currentUserGuessNumber = currentUserGuessNumber.substring(
                0,
                currentUserGuessNumber.length - 1
            )
            while (currentUserGuessNumber.length < 4) {
                currentUserGuessNumber += "_"
            }
            _uiState.update { currentState ->
                currentState.copy(
                    currentUserGuessNumber = currentUserGuessNumber,
                )
            }
        }
    }


    fun timer() {
        timerJob = viewModelScope.launch {
            var elapsedTime = uiState.value.elapsedTime
            while (true) {
                if (!uiState.value.isRunning) break
                delay(1000) // Затримка на 1 секунду
                elapsedTime++
                _uiState.update { currentState ->
                    currentState.copy(
                        elapsedTime = elapsedTime,
                    )
                }
            }
        }
    }

    fun formatSeconds(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun editCurrentGuessNumber(symbol: String) {
        var currentUserGuessNumber: String =
            uiState.value.currentUserGuessNumber.filterNot { it == '_' }
        if (currentUserGuessNumber.length < 3 && !currentUserGuessNumber.contains(symbol)) {
            currentUserGuessNumber += symbol
            while (currentUserGuessNumber.length < 4) {
                currentUserGuessNumber += "_"
            }
            _uiState.update { currentState ->
                currentState.copy(
                    currentUserGuessNumber = currentUserGuessNumber
                )
            }
        } else if (!currentUserGuessNumber.contains(symbol)) {
            currentUserGuessNumber += symbol
            _uiState.update { currentState ->
                currentState.copy(
                    currentUserGuessNumber = currentUserGuessNumber
                )
            }
            checkUserGuess(currentUserGuessNumber, currentHiddenNumber)
        }
    }

    fun newGame() {
        allSymbolFalse()
        currentHiddenNumber = getRandomNumber()
        _uiState.update { currentState ->
            currentState.copy(
                currentHiddenNumber = currentHiddenNumber,
                currentUserGuessNumber = "____",
                guesses = mutableMapOf(),
                gameOver = false,
                remainingGuesses = 10,
                isRunning = true,
                elapsedTime = 0
            )
        }
        timer()
    }

    init {
        newGame()
    }
}