package com.example.cruditemapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cruditemapp.data.Item
import com.example.cruditemapp.ui.theme.CrudItemAppTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrudItemAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Chamando a nova tela principal
                    ImprovedItemScreen()
                }
            }
        }
    }
}

// Classe selada para gerenciar os diferentes estados da UI
sealed class UiState {
    object Loading : UiState()
    data class Success(val items: List<Item>) : UiState()
    data class Error(val message: String) : UiState()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedItemScreen() {
    // --- ESTADOS DA UI ---
    var uiState by remember { mutableStateOf<UiState>(UiState.Loading) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados para controlar diálogos e o bottom sheet
    var showAddItemSheet by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Item?>(null) }
    var deletingItem by remember { mutableStateOf<Item?>(null) }


    // --- REFERÊNCIA AO FIREBASE ---
    val itemsCollection = remember { Firebase.firestore.collection("items") }

    // --- LÓGICA DE LEITURA (EM TEMPO REAL) ---
    LaunchedEffect(Unit) {
        itemsCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Firebase", "Erro ao escutar por mudanças.", error)
                uiState = UiState.Error("Falha ao carregar os dados. Tente novamente.")
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val itemsList = snapshots.documents.map { document ->
                    document.toObject(Item::class.java)!!.copy(id = document.id)
                }
                uiState = UiState.Success(itemsList)
                Log.d("Firebase", "Lista de itens atualizada. Total: ${itemsList.size}")
            }
        }
    }

    // --- FUNÇÕES CRUD ---
    fun addItem(title: String, description: String) {
        val newItem = Item(title = title, description = description)
        itemsCollection.add(newItem)
            .addOnSuccessListener {
                Log.d("Firebase", "SUCESSO! Item adicionado.")
                scope.launch { snackbarHostState.showSnackbar("Item adicionado com sucesso!") }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "FALHA ao adicionar item.", e)
                scope.launch { snackbarHostState.showSnackbar("Erro ao adicionar o item.") }
            }
    }

    fun updateItem(item: Item) {
        item.id?.let {
            itemsCollection.document(it).set(item)
                .addOnSuccessListener {
                    Log.d("Firebase", "SUCESSO! Item atualizado.")
                    scope.launch { snackbarHostState.showSnackbar("Item atualizado com sucesso!") }
                    editingItem = null // Fecha o diálogo
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "FALHA ao atualizar item.", e)
                    scope.launch { snackbarHostState.showSnackbar("Erro ao atualizar o item.") }
                }
        }
    }

    fun performDelete(item: Item) {
        item.id?.let {
            itemsCollection.document(it).delete()
                .addOnSuccessListener {
                    Log.d("Firebase", "SUCESSO! Item apagado.")
                    scope.launch { snackbarHostState.showSnackbar("Item apagado.") }
                    deletingItem = null // Fecha o diálogo de confirmação
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "FALHA ao apagar item.", e)
                    scope.launch { snackbarHostState.showSnackbar("Erro ao apagar o item.") }
                }
        }
    }

    // --- INTERFACE DO USUÁRIO (UI) COM SCAFFOLD ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Itens (CRUD App)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddItemSheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Item")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // Controla a exibição com base no UiState
            when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                is UiState.Success -> {
                    if (state.items.isEmpty()) {
                        Text(
                            text = "Nenhum item encontrado.\nToque no botão '+' para adicionar o primeiro!",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.items, key = { it.id!! }) { item ->
                                ItemCard(
                                    item = item,
                                    onEditClick = { editingItem = item },
                                    onDeleteClick = { deletingItem = item }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- BOTTOM SHEET PARA ADICIONAR ITEM ---
    if (showAddItemSheet) {
        AddItemBottomSheet(
            onDismiss = { showAddItemSheet = false },
            onSave = { title, description ->
                addItem(title, description)
                showAddItemSheet = false
            }
        )
    }

    // --- DIÁLOGO DE EDIÇÃO ---
    if (editingItem != null) {
        EditItemDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { updatedItem ->
                updateItem(updatedItem)
            }
        )
    }

    // --- DIÁLOGO DE CONFIRMAÇÃO DE EXCLUSÃO ---
    if (deletingItem != null) {
        ConfirmationDialog(
            onConfirm = { performDelete(deletingItem!!) },
            onDismiss = { deletingItem = null }
        )
    }
}


@Composable
fun ItemCard(item: Item, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                // Esconde a descrição se ela estiver em branco
                AnimatedVisibility(visible = item.description.isNotBlank()) {
                    Text(item.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar Item", tint = MaterialTheme.colorScheme.secondary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Apagar Item", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Adicionar Novo Item", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(bottom = 16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = it.isBlank()
                },
                label = { Text("Título*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = titleError,
                supportingText = {
                    if (titleError) {
                        Text("O título é obrigatório")
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição (Opcional)") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, description)
                    } else {
                        titleError = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Item")
            }
        }
    }
}

// O diálogo de edição permanece muito similar, você pode reutilizá-lo ou usar este.
@Composable
fun EditItemDialog(item: Item, onDismiss: () -> Unit, onSave: (Item) -> Unit) {
    var title by remember { mutableStateOf(item.title) }
    var description by remember { mutableStateOf(item.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(item.copy(title = title, description = description)) }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Exclusão") },
        text = { Text("Você tem certeza de que deseja apagar este item? Esta ação não pode ser desfeita.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Apagar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}