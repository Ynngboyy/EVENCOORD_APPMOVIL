package com.example.eventcoord.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewEventScreen(onBackClick: () -> Unit) {
    var nom_even by remember { mutableStateOf("")}
    var fecha by remember { mutableStateOf("")}
    var invitados by remember { mutableStateOf("")}
    val seleccionados = remember { mutableStateListOf<String>() }
    val opciones = listOf("Evento Social", "Boda", "XV años", "Bautizo", "Cumpleaños")

    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Volver",
                            tint = Color(0xFF2196F3)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Volver",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A4F55)
                    )
                }
            },
            bottomBar = {
                Card(modifier = Modifier.fillMaxWidth().navigationBarsPadding(), colors = CardDefaults.cardColors(containerColor = Color.Black/*(0xFFC6A45C)*/), shape = androidx.compose.ui.graphics.RectangleShape) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                    ) {
                        Spacer(modifier= Modifier.width(5.dp))
                        TextButton(
                            onClick = {}
                        ) {
                            Text(
                                text = "Crear Evento",
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF4A4F55)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize() //
                    .padding(innerPadding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("NUEVO EVENTO")
                Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = nom_even,
                        onValueChange = { nom_even = it },
                        label = { Text("Nombre del evento") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = fecha,
                        onValueChange = { fecha = it },
                        label = { Text("fecha del evento") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = invitados,
                        onValueChange = { invitados = it },
                        label = { Text("cantidad de invitados") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("PLANTILLAS")
                    Spacer(modifier = Modifier.height(16.dp))
                    opciones.forEach { opcion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .selectable(
                                    selected = seleccionados.contains(opcion),
                                    onClick = {
                                        if (seleccionados.contains(opcion)) {
                                            seleccionados.remove(opcion)
                                        } else {
                                            seleccionados.add(opcion)
                                        }
                                    }
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = seleccionados.contains(opcion),
                                onCheckedChange = null, // null porque el Row ya maneja el clic
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF2196F3)
                                )
                            )
                            Text(
                                text = opcion,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

            }
        }
    }
}