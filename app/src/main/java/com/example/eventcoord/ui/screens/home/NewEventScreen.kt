package com.example.eventcoord.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.core.content.edit
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape

@Composable
fun NewEventScreen(onBackClick: () -> Unit) {
    var actualSection by remember { mutableStateOf("Principal") }
    var nom_even by remember { mutableStateOf("")}
    var fecha by remember { mutableStateOf("")}
    var invitados by remember { mutableStateOf("")}
    var isVisible by remember { mutableStateOf(false) }


        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                val topBarTitle = if (actualSection == "Principal") "Volver" else actualSection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick ={ if (actualSection == "Principal") onBackClick() else actualSection = "Principal"}) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Volver",
                            tint = Color(0xFF2196F3)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = topBarTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A4F55)
                    )
                }
            },
            bottomBar = {
                MenuBoton()
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize() //
                    .padding(innerPadding)
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when (actualSection){
                    "Principal" ->{
                        Text("PLANTILLAS")
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(

                            horizontalArrangement = Arrangement.Center
                        ) {
                            Plantillas("XV años",Color(0xFF4A4F55),Color(0xFFF4F1EA)) {
                                actualSection = "XV años"
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Plantillas("Boda",Color(0xFFF4F1EA),Color(0xFF4A4F55)) {
                                actualSection = "Boda"
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Plantillas("Graduacion",Color(0xFF243F63),Color(0xFFF4F1EA)) {
                                actualSection = "Graduacion"
                            }
                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "NUEVO EVENTO")
                            Spacer(modifier = Modifier.width(8.dp))
                            Button( onClick = { isVisible = !isVisible}) {
                                Text("Abrir formulario")
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        AnimatedVisibility(visible = isVisible) {
                            Nuevo("","",Color(0xFFE8E6E1), Color(0xFF4A4F55))
                        }
                    }
                    "XV años" -> XVseccion()
                    "Boda" -> BodaSeccion()
                    "Graduacion" -> GraduacionSeccion()
                }

            }
        }

}

@Composable
fun Plantillas(text: String, containerColor: Color, contentColor: Color, onClick: () -> Unit){
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(98.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,

                color = contentColor
            )
        }
    }
}

@Composable
fun XVseccion(){
    Nuevo(
        tipoEventoInicial = "XV Años",
        notasIniciales = "1. Recepción\n2. Vals Principal\n3. Brindis",
        containerColor = Color(0xFFF4F1EA),
        contentColor = Color(0xFF4A4F55)
    )
}

@Composable
fun BodaSeccion(){
    Nuevo(
        tipoEventoInicial = "Boda",
        notasIniciales = "1. Ceremonia\n2. Banquete\n3. Fiesta",
        containerColor = Color(0xFFF4F1EA),
        contentColor = Color(0xFF4A4F55)
    )
}

@Composable
fun GraduacionSeccion(){
    Nuevo(
        tipoEventoInicial = "Graduacion",
        notasIniciales = "1. Ceremonia\n2. Banquete\n3. Grupo musical",
        containerColor = Color(0xFFF4F1EA),
        contentColor = Color(0xFF4A4F55)
    )
}

@Composable
fun Nuevo(
    tipoEventoInicial: String = "",
    notasIniciales: String = "",
    containerColor: Color,
    contentColor: Color
) {
    var nombre by remember { mutableStateOf("") }
    var tipoEvento by remember { mutableStateOf(tipoEventoInicial) }
    var fecha by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf(notasIniciales) }

    val scrollState = rememberScrollState()//desplazamiento

    val actividadesEjemplo = listOf( //ejemplo de programa que se obtendra al llenar datos
        Actividad("18:00", "Recepción", "Bienvenida de invitados.", true),
        Actividad("19:30", "Protocolo Principal", "Vals o Brindis."),
        Actividad("21:00", "Cena", "Servicio de banquete."),
        Actividad("22:30", "Baile", "Apertura de pista.")
    )

    Card(
        modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp), // Limitamos la altura para que no tape todo
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (tipoEventoInicial.isEmpty()) "Detalles del Nuevo Evento" else "Configurar Plantilla",
                style = MaterialTheme.typography.titleLarge,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del evento") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tipoEvento,
                onValueChange = { tipoEvento = it },
                label = { Text("Tipo de Evento") },
                modifier = Modifier.fillMaxWidth(),
                enabled = tipoEventoInicial.isEmpty() // Si viene de plantilla, lo bloqueamos
            )
            // Campo: Fecha
            OutlinedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = { Text("Fecha (DD/MM/AAAA)") },
                modifier = Modifier.fillMaxWidth()
            )
            // Campo: Ubicación
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación / Salón") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas/Programa") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Programa Sugerido",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4A4F55),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // se crea la linea del tiempo dentro del mismo scroll
            actividadesEjemplo.forEachIndexed { index, actividad ->
                FilaDeActividad(
                    actividad = actividad,
                    esElUltimo = index == actividadesEjemplo.size - 1
                )
            }

            Button(onClick = { /* Lógica de Firebase aquí */ }) {
                Text("Confirmar Evento")
            }
        }
    }
}

@Composable //elementos de la linea del tiempo
fun FilaDeActividad(actividad: Actividad, esElUltimo: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(30.dp)
        ) {
            Box(
                modifier = Modifier.size(10.dp).background(
                    color = if (actividad.esImportante) Color(0xFF2196F3) else Color.Gray,
                    shape = CircleShape
                )
            )
            if (!esElUltimo) {
                Box(
                    modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray)
                )
            }
        }

        Column(modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)) {
            Text(text = actividad.hora, fontSize = 11.sp, color = Color.Gray)
            Text(text = actividad.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = actividad.descripcion, fontSize = 13.sp, color = Color.DarkGray)
        }
    }
}
data class Actividad(val hora: String, val titulo: String, val descripcion: String, val esImportante: Boolean = false)



@Composable
fun MenuBoton(){
    Card(modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding(), colors = CardDefaults.cardColors(containerColor = Color.Black/*(0xFFC6A45C)*/), shape = androidx.compose.ui.graphics.RectangleShape) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
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

