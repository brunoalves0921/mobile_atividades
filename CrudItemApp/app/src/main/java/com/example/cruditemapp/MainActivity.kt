package com.example.cruditemapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cruditemapp.data.Item
import com.example.cruditemapp.ui.theme.CrudItemAppTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CrudItemAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ItemScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen() {
    // --- ESTADOS DA UI ---
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<Item>() }

    // Estado para controlar o item sendo editado e a visibilidade do diálogo
    var editingItem by remember { mutableStateOf<Item?>(null) }

    // --- REFERÊNCIA AO FIREBASE ---
    val itemsCollection = Firebase.firestore.collection("items")

    // --- LÓGICA DE LEITURA (EM TEMPO REAL) ---
    // LaunchedEffect escuta por mudanças no Firestore e atualiza a lista 'items' automaticamente.
    LaunchedEffect(Unit) {
        itemsCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Firebase", "Erro ao escutar por mudanças no Firestore.", error)
                return@addSnapshotListener
            }

            items.clear()
            if (snapshots != null) {
                for (document in snapshots) {
                    val item = document.toObject(Item::class.java).copy(id = document.id)
                    items.add(item)
                }
                Log.d("Firebase", "Lista de itens atualizada. Total de itens: ${items.size}")
            }
        }
    }

    // --- LÓGICA DE ESCRITA (CREATE) ---
    fun addItem() {
        if (title.isNotBlank() && description.isNotBlank()) {
            Log.d("Firebase", "Botão 'Add Item' clicado. Tentando adicionar: Título='$title'")
            val newItem = Item(title = title, description = description)
            itemsCollection.add(newItem) // [cite: 524]
                .addOnSuccessListener { documentReference ->
                    Log.d("Firebase", "SUCESSO! Item adicionado com ID: ${documentReference.id}")
                    title = ""
                    description = ""
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "FALHA ao adicionar item.", e)
                }
        } else {
            Log.w("Firebase", "Tentativa de adicionar item com campos vazios.")
        }
    }

    // --- LÓGICA DE APAGAR (DELETE) ---
    fun deleteItem(itemId: String) {
        Log.d("Firebase", "Tentando apagar item com ID: $itemId")
        itemsCollection.document(itemId).delete()
            .addOnSuccessListener {
                Log.d("Firebase", "SUCESSO! Item apagado.")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "FALHA ao apagar item.", e)
            }
    }

    // --- INTERFACE DO USUÁRIO (UI) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- SEÇÃO DE INPUT ---
        Text("Title", style = MaterialTheme.typography.titleMedium) // [cite: 599]
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Description", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { addItem() }, modifier = Modifier.fillMaxWidth()) {
            Text("Add Item") // [cite: 601]
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()

        // --- LISTA DE ITENS ---
        LazyColumn { // [cite: 552]
            items(items) { item -> // [cite: 553]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Title: ${item.title}") // [cite: 606, 609, 616]
                        Text("Description: ${item.description}") // [cite: 606, 609, 616]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            // Botão de Update agora abre o diálogo
                            TextButton(onClick = { editingItem = item }) { // [cite: 608, 611, 618]
                                Text("Update")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            // Botão de Delete agora chama a função deleteItem
                            TextButton(onClick = { item.id?.let { deleteItem(it) } }) { // [cite: 607, 610, 617]
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIÁLOGO DE UPDATE ---
    // Este diálogo só aparece quando 'editingItem' não for nulo
    if (editingItem != null) {
        EditItemDialog(
            item = editingItem!!,
            onDismiss = { editingItem = null },
            onSave = { updatedItem ->
                Log.d("Firebase", "Tentando salvar item atualizado com ID: ${updatedItem.id}")
                updatedItem.id?.let {
                    itemsCollection.document(it).set(updatedItem)
                        .addOnSuccessListener {
                            Log.d("Firebase", "SUCESSO! Item atualizado.")
                            editingItem = null
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "FALHA ao atualizar item.", e)
                        }
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemDialog(item: Item, onDismiss: () -> Unit, onSave: (Item) -> Unit) {
    var title by remember { mutableStateOf(item.title) }
    var description by remember { mutableStateOf(item.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Item") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedItem = item.copy(title = title, description = description)
                    onSave(updatedItem)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}