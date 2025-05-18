package com.example.planetapp

import NavGraph
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavGraph(
                onSettingsClick = {
// Ação para Configurações (pode abrir uma nova tela ou exibir um diálogo)
                },
                onHelpClick = {
// Ação para Ajuda (pode abrir uma nova tela ou exibir um diálogo)
                }
            )
        }
    }
}