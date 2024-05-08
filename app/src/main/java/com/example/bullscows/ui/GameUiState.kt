package com.example.bullscows.ui

data class GameUiState(
    val numbers: MutableMap<String, Boolean> = mutableMapOf(
        "1" to false,
        "2" to false,
        "3" to false,
        "4" to false,
        "5" to false,
        "6" to false,
        "7" to false,
        "8" to false,
        "9" to false,
        "0" to false,
    ),
    var remainingGuesses: Int = 10,
    var currentHiddenNumber: String = "",
    var currentUserGuessNumber: String = "____",
    var guesses: MutableMap<String, String> = mutableMapOf(),
    var gameOver: Boolean = false,
    var gameWin: Boolean = false,
    var elapsedTime: Int =0,
    var isRunning: Boolean =false,
    var finalScore:String=""

) {

}
