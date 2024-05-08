package com.example.bullscows.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GameScreen() {
    val gameViewModel: GameViewModel = viewModel()
    val gameUiState by gameViewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Elapsed time " + gameViewModel.formatSeconds(gameUiState.elapsedTime))
            Text(text = "Remaining steps: " + gameUiState.remainingGuesses.toString())
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(gameUiState.guesses.size) {
                Row {
                    val entry = gameUiState.guesses.entries.reversed()[it]
                    GuessesResult(guess = entry, numbersState = gameUiState.numbers, gameViewModel)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
        Text(text = gameUiState.currentUserGuessNumber, style = typography.displayLarge)
        Column() {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton(gameViewModel, 1)
                NumberButton(gameViewModel, 2)
                NumberButton(gameViewModel, 3)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton(gameViewModel, 4)
                NumberButton(gameViewModel, 5)
                NumberButton(gameViewModel, 6)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton(gameViewModel, 7)
                NumberButton(gameViewModel, 8)
                NumberButton(gameViewModel, 9)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(icon = Icons.Default.Refresh) {
                    gameViewModel.newGame()
                }
                NumberButton(gameViewModel, 0)
                IconButton(icon = Icons.Default.ArrowBack) {
                    gameViewModel.deleteLastSymbolUserGuess()
                }
            }
        }
    }

    if (gameUiState.gameOver) {
        val activity = (LocalContext.current as Activity)
        val result = if (gameUiState.gameWin) {
            "You win. Your score is ${gameUiState.finalScore}"
        } else {
            "You loose"
        }
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onCloseRequest.
            },
            title = { Text("Game over") },
            text = { Text(result) },
            dismissButton = {
                TextButton(
                    onClick = {
                        activity.finish()
                    }
                ) {
                    Text(text = "Exit")
                }
            },
            confirmButton = {
                TextButton(onClick = { gameViewModel.newGame() }) {
                    Text(text = "New game")
                }
            }
        )
    }

}

@Composable
private fun GuessesResult(
    guess: MutableMap.MutableEntry<String, String>,
    numbersState: Map<String, Boolean>, viewModel: GameViewModel
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (key in guess.key) {
            val color = if (numbersState[key.toString()] == true) {
                colorScheme.inversePrimary
            } else {
                colorScheme.onPrimary
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color, shape = CircleShape)
                    .clickable { viewModel.toggleSymbolState(key.toString()) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = key.toString(),
                    modifier = Modifier,
                    style = typography.headlineLarge
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
        }
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            guess.value, style = typography.headlineLarge
        )
    }

}

@Composable
private fun NumberButton(gameViewModel: GameViewModel, number: Int) {
    Button(modifier = Modifier
        .size(85.dp)
        .padding(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimaryContainer
        ),
        onClick = { gameViewModel.editCurrentGuessNumber(number.toString()) }) {
        Text(
            text = number.toString(),
            style = typography.headlineLarge
        )
    }
}

@Composable
private fun IconButton(icon: ImageVector, onclick: () -> Unit) {
    Button(
        modifier = Modifier
            .size(85.dp)
            .padding(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimaryContainer
        ),
        onClick = onclick
    ) {
        Icon(imageVector = icon, contentDescription = "")
    }

}