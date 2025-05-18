package com.example.planetapp.models
import com.example.planetapp.R
data class Planet(
    val id: Int,
    val name: String,
    val type: String,
    val galaxy: String,
    val distanceFromSun: String,
    val diameter: String,
    val characteristics: String,
    val imageRes: Int,
    var isFavorite: Boolean = false
)

val planetList = listOf(
    Planet(
        id = 1,
        name = "Mercúrio",
        type = "Terrestre",
        galaxy = "Via Láctea",
        distanceFromSun = "57,9 milhões de km",
        diameter = "4.879 km",
        characteristics = "Menor planeta, sem atmosfera significativa, temperaturas extremas.",
        imageRes = R.drawable.mercurio
    ),
    Planet(
        id = 2,
        name = "Vênus",
        type = "Terrestre",
        galaxy = "Via Láctea",
        distanceFromSun = "108,2 milhões de km",
        diameter = "12.104 km",
        characteristics = "Planeta mais quente, atmosfera densa e tóxica.",
        imageRes = R.drawable.venus
    ),
    Planet(
        id = 3,
        name = "Terra",
        type = "Terrestre",
        galaxy = "Via Láctea",
        distanceFromSun = "149,6 milhões de km",
        diameter = "12.742 km",
        characteristics = "Suporta vida, possui água e atmosfera.",
        imageRes = R.drawable.terra
    ),
    Planet(
        id = 4,
        name = "Marte",
        type = "Terrestre",
        galaxy = "Via Láctea",
        distanceFromSun = "227,9 milhões de km",
        diameter = "6.779 km",
        characteristics = "Conhecido como Planeta Vermelho, pode ter tido água.",
        imageRes = R.drawable.marte
    ),
    Planet(
        id = 5,
        name = "Júpiter",
        type = "Gigante Gasoso",
        galaxy = "Via Láctea",
        distanceFromSun = "778,5 milhões de km",
        diameter = "139.820 km",
        characteristics = "Maior planeta, possui a Grande Mancha Vermelha e muitas luas.",
        imageRes = R.drawable.jupiter
    ),
    Planet(
        id = 6,
        name = "Saturno",
        type = "Gigante Gasoso",
        galaxy = "Via Láctea",
        distanceFromSun = "1,43 bilhão de km",
        diameter = "116.460 km",
        characteristics = "Famoso por seus anéis, possui muitas luas.",
        imageRes = R.drawable.saturno
    ),
    Planet(
        id = 7,
        name = "Urano",
        type = "Gigante de Gelo",
        galaxy = "Via Láctea",
        distanceFromSun = "2,87 bilhões de km",
        diameter = "50.724 km",
        characteristics = "Gira de lado, possui anéis tênues.",
        imageRes = R.drawable.urano
    ),
    Planet(
        id = 8,
        name = "Netuno",
        type = "Gigante de Gelo",
        galaxy = "Via Láctea",
        distanceFromSun = "4,5 bilhões de km",
        diameter = "49.244 km",
        characteristics = "Possui os ventos mais fortes do Sistema Solar, cor azul intensa.",
        imageRes = R.drawable.netuno
    )
)