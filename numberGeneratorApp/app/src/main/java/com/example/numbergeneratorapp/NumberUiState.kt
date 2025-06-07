package com.example.numbergeneratorapp

sealed class NumberUiState {
    object Idle: NumberUiState()
    object Loading: NumberUiState()
    data class Success(val number: Int): NumberUiState()
    data class Error(val message: String): NumberUiState()
}