package com.example.investidorapp // Use o seu nome de pacote

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.investidorapp.ui.theme.InvestidorAppTheme // Pode precisar ajustar o import do tema
import com.example.investidorapp.view.InvestidorScreen
import com.example.investidorapp.viewmodel.InvestimentosViewModel

class MainActivity : ComponentActivity() { // [cite: 1158]
    private val viewModel: InvestimentosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) { // [cite: 1158]
        super.onCreate(savedInstanceState)
        // Solicita permissão de notificação para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // [cite: 1159]
            ActivityCompat.requestPermissions( // [cite: 1160]
                    this, // [cite: 1161]
            arrayOf(Manifest.permission.POST_NOTIFICATIONS), // [cite: 1162]
            101
            )
        }
        setContent { // [cite: 1164]
            InvestidorAppTheme {
                InvestidorScreen(viewModel = viewModel) // [cite: 1165]
            }
        }
    }
}