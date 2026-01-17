package com.lettrus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.lettrus.data.dictionary.DictionaryRepositoryImpl
import com.lettrus.presentation.GameViewModel
import com.lettrus.ui.screens.GameScreen
import com.lettrus.ui.theme.LettrusTheme

@Composable
fun App() {
    val dictionaryRepository = remember { DictionaryRepositoryImpl() }
    val viewModel = remember { GameViewModel(dictionaryRepository) }

    LettrusTheme {
        GameScreen(viewModel = viewModel)
    }
}
