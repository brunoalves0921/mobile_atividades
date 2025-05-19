package com.example.nighteventsapp.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.nighteventsapp.R // Certifique-se que o R é importado corretamente

// Sua data class Event (mantida como no seu código original)
data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val isFavorite: MutableState<Boolean> = mutableStateOf(false),
    val isSubscribed: MutableState<Boolean> = mutableStateOf(false),
    val imageRes: Int
)

val eventList = listOf(
    Event(
        id = 1,
        title = "Conferência de Tecnologia 2024",
        description = "Tendências em inteligência artificial, desenvolvimento e nuvem.",
        date = "2024-12-15",
        location = "Parque Tecnológico Central",
        imageRes = R.drawable.img2 // Assumindo que img2 existe
    ),
    Event(
        id = 2,
        title = "Workshop de Design Gráfico",
        description = "Aprenda a criar designs impactantes com as últimas ferramentas.",
        date = "2024-11-20",
        location = "Teatro Municipal das Artes",
        imageRes = R.drawable.img1 // Assumindo que img1 existe
    ),
    Event(
        id = 3,
        title = "Festival de Música Indie",
        description = "Bandas emergentes e artistas consagrados em dois dias de música.",
        date = "2025-01-18",
        location = "Arena Verde Vale",
        imageRes = R.drawable.img3 // Adicione img3 ao seu res/drawable
    ),
    Event(
        id = 4,
        title = "Feira Gastronômica Sabores do Mundo",
        description = "Experimente pratos típicos de diversos países e culturas.",
        date = "2025-02-22",
        location = "Centro de Convenções Gastronômico",
        imageRes = R.drawable.img4 // Adicione img4
    ),
    Event(
        id = 5,
        title = "Maratona de Programação HackFest",
        description = "Desafie suas habilidades de codificação e crie soluções inovadoras.",
        date = "2025-03-10",
        location = "Universidade de Tecnologia Avançada",
        imageRes = R.drawable.img5 // Adicione img5
    ),
    Event(
        id = 6,
        title = "Exposição de Arte Moderna 'Visões'",
        description = "Obras de artistas contemporâneos explorando novas perspectivas.",
        date = "2025-04-05",
        location = "Galeria de Arte Principal",
        imageRes = R.drawable.img6 // Adicione img6
    ),
    Event(
        id = 7,
        title = "Show de Stand-up Comedy",
        description = "Noite de muitas risadas com os melhores comediantes da atualidade.",
        date = "2025-05-12",
        location = "Clube da Comédia Central",
        imageRes = R.drawable.img7 // Adicione img7
    ),
    Event(
        id = 8,
        title = "Curso Intensivo de Fotografia Digital",
        description = "Domine sua câmera e aprenda técnicas de composição e edição.",
        date = "2025-06-01",
        location = "Estúdio Fotográfico Criativo",
        imageRes = R.drawable.img8 // Adicione img8
    ),
    Event(
        id = 9,
        title = "Campeonato de eSports - Liga Alpha",
        description = "Os melhores jogadores competindo pelos títulos em diversos jogos.",
        date = "2025-07-20",
        location = "Arena Gamer Pro",
        imageRes = R.drawable.img9 // Adicione img9
    ),
    Event(
        id = 10,
        title = "Palestra sobre Sustentabilidade Urbana",
        description = "Debates e soluções para cidades mais verdes e sustentáveis.",
        date = "2025-08-15",
        location = "Auditório Municipal Verde",
        imageRes = R.drawable.img10 // Adicione img10
    )
)