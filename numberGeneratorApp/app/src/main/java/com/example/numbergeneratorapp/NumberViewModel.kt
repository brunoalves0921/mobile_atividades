package com.example.numbergeneratorapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class NumberViewModel: ViewModel() {
    var uiState by mutableStateOf<NumberUiState>(NumberUiState.Idle)
        private set

    fun generateNumber() {
        viewModelScope.launch {
            uiState = NumberUiState.Loading
            delay(2000)

//            if (Random.nextBoolean()) {
//                uiState = NumberUiState.Erro
//                return@launchr  ("Erro ao gerar o número, tente novamente")
//            }
//
            val randomNumber = (1..100).random()
            uiState = NumberUiState.Success(randomNumber)
        }
    }

    fun reset() {
        uiState = NumberUiState.Idle
    }
}