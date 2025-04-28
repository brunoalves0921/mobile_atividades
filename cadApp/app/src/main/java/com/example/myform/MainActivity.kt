package com.example.myform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.example.myform.ui.theme.MyFormTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFormTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyForm()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyForm() {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }
    var datePickerOpen by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Sobrenome") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        LaunchedEffect(hasFocus) {
            if (hasFocus && !datePickerOpen) {
                datePickerOpen = true
            }
        }

        // Date Picker
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerOpen = true }
        ) {
            OutlinedTextField(
                value = birthDate,
                onValueChange = {},
                label = { Text("Data de Nascimento") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        hasFocus = focusState.isFocused
                    }
                    .focusRequester(focusRequester),
                enabled = true
            )
        }

        if (datePickerOpen) {
            DatePickerDialog(
                onDismissRequest = { datePickerOpen = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerOpen = false
                            val selectedDateMillis = datePickerState.selectedDateMillis
                            if (selectedDateMillis != null) {
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = selectedDateMillis
                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                birthDate = dateFormat.format(calendar.time)
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { datePickerOpen = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Gender Dropdown
        ExposedDropdownMenuBox(
            expanded = genderExpanded,
            onExpandedChange = { genderExpanded = !genderExpanded }
        ) {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                readOnly = true,
                label = { Text("Gênero") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = genderExpanded,
                onDismissRequest = { genderExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Homem") },
                    onClick = {
                        gender = "Homem"
                        genderExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Mulher") },
                    onClick = {
                        gender = "Mulher"
                        genderExpanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            label = { Text("Celular") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Endereço") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Número") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = district,
            onValueChange = { district = it },
            label = { Text("Bairro") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Cidade") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state,
            onValueChange = { state = it },
            label = { Text("Estado") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = zipCode,
            onValueChange = { zipCode = it },
            label = { Text("CEP") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nationality,
            onValueChange = { nationality = it },
            label = { Text("Nacionalidade") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = profession,
            onValueChange = { profession = it },
            label = { Text("Profissão") },
            modifier = Modifier.fillMaxWidth()
            )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Nada por enquanto
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                // Limpa todos os campos
                name = ""
                lastName = ""
                birthDate = ""
                gender = ""
                phone = ""
                mobile = ""
                email = ""
                address = ""
                number = ""
                district = ""
                city = ""
                state = ""
                zipCode = ""
                nationality = ""
                profession = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Limpar")
        }


    }
}
