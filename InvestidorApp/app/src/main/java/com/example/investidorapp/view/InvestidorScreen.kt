package com.example.investidorapp.view // Use o seu nome de pacote

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.investidorapp.model.Investimento
import com.example.investidorapp.viewmodel.InvestimentosViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestidorScreen(viewModel: InvestimentosViewModel) {
    val investimentos by viewModel.investimentos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minha Carteira") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (investimentos.isNotEmpty()) {
                ResumoCarteira(investimentos)
                Spacer(modifier = Modifier.height(16.dp))
            }
            ListaInvestimentos(investimentos = investimentos)
        }
    }
}

@Composable
fun ResumoCarteira(investimentos: List<Investimento>) {
    val valorTotal = investimentos.sumOf { it.valor }
    val numeroDeAtivos = investimentos.size

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumo da Carteira",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Valor Total", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = formatCurrency(valorTotal),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Nº de Ativos", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "$numeroDeAtivos",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ListaInvestimentos(investimentos: List<Investimento>, modifier: Modifier = Modifier) {
    if (investimentos.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // MUDANÇA AQUI: Calculamos o valor total uma única vez
        val valorTotal = investimentos.sumOf { it.valor }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(investimentos) { investimento ->
                // MUDANÇA AQUI: Passamos o valor total para cada item
                InvestimentoItem(
                    investimento = investimento,
                    valorTotalCarteira = valorTotal
                )
            }
        }
    }
}

@Composable
fun InvestimentoItem(investimento: Investimento, valorTotalCarteira: Long) { // MUDANÇA AQUI: Novo parâmetro
    // Lógica para calcular a porcentagem do ativo em relação ao total
    val progresso = if (valorTotalCarteira > 0) {
        (investimento.valor.toFloat() / valorTotalCarteira.toFloat())
    } else {
        0f // Evita divisão por zero
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getIconForInvestimento(investimento.nome),
                contentDescription = "Ícone de ${investimento.nome}",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = investimento.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatCurrency(investimento.valor),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            // MUDANÇA AQUI: Passamos o progresso já calculado
            IndicadorProgressoValor(progresso = progresso)
        }
    }
}

@Composable
fun IndicadorProgressoValor(progresso: Float) { // MUDANÇA AQUI: Recebe o progresso direto
    val progressoAnimado by animateFloatAsState(
        targetValue = progresso, // Usa o progresso que foi calculado
        label = "progresso_animado"
    )
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = progressoAnimado,
            modifier = Modifier.size(50.dp),
            strokeWidth = 4.dp,
            strokeCap = StrokeCap.Round
        )
        Text(
            // Mostra a porcentagem correta
            text = "${(progressoAnimado * 100).toInt()}%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Funções utilitárias (sem alteração)
private fun formatCurrency(value: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(value)
}

private fun getIconForInvestimento(nome: String): ImageVector {
    return when {
        nome.contains("Bitcoin", ignoreCase = true) -> Icons.Default.MonetizationOn
        nome.contains("Ações", ignoreCase = true) -> Icons.Default.ShowChart
        nome.contains("Tesla", ignoreCase = true) -> Icons.Default.Business
        else -> Icons.Default.AccountBalanceWallet
    }
}