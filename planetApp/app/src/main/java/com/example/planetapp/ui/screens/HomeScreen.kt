import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planetapp.models.Planet
import com.example.planetapp.models.planetList
import com.example.planetapp.ui.components.PlanetListItem

@ExperimentalMaterial3Api
@Composable
fun HomeScreen (
    onPlanetSelected : (Planet) -> Unit,
    onSettingsClick : () -> Unit,
    onHelpClick : () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredPlanets = remember (searchQuery ) {
        planetList.filter { it.name.contains(searchQuery , ignoreCase = true) }
    }
    val recentSearches = remember { mutableStateListOf<Planet >() }
    Scaffold (
        topBar = {
            TopAppBarWithMenu (
                onSettingsClick = onSettingsClick ,
                onHelpClick = onHelpClick
            )
        }
    ) { innerPadding ->
        Column (modifier = Modifier .padding(innerPadding )) {
// Barra de pesquisa
            TextField (
                value = searchQuery ,
                onValueChange = { searchQuery = it },
                label = { Text("Pesquisar" ) },
                modifier = Modifier
                    .fillMaxWidth()
                    . padding(8.dp)
            )

// LazyRow para buscas recentes
            LazyRow (
                modifier = Modifier .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement .spacedBy( 8.dp)
            ) {
                items(recentSearches ) { planet ->
                    Button (onClick = { onPlanetSelected (planet ) }) {
                        Text(planet .name)
                    }
                }
            }
// LazyColumn para lista de planetas filtrados
            LazyColumn (
                verticalArrangement = Arrangement .spacedBy( 8.dp),
                modifier = Modifier .padding(horizontal = 8.dp)
            ) {
                items(filteredPlanets ) { planet ->
                    PlanetListItem (
                        planet = planet ,

                        onPlanetSelected = { selectedPlanet ->
                            if (!recentSearches .contains( selectedPlanet )) {
                                recentSearches .add( 0, selectedPlanet )
                            }
                            onPlanetSelected (selectedPlanet )
                        },

                        onFavoriteToggle = { favoritePlanet ->

                            favoritePlanet .isFavorite = !favoritePlanet .isFavorite
                        }
                    )
                }
            }
        }
    }
}