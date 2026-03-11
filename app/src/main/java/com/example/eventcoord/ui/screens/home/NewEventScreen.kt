package com.example.eventcoord.ui.screens.home

import android.R.attr.enabled
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CheckboxDefaults.colors
import com.example.eventcoord.data.model.Actividad
import com.example.eventcoord.data.model.Evento
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

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
        notasIniciales = "codigo de vestimenta formal-casual",
        containerColor = Color(0xFFF4F1EA),
        contentColor = Color(0xFF4A4F55)
    )
}

@Composable
fun BodaSeccion(){
    Nuevo(
        tipoEventoInicial = "Boda",
        notasIniciales = "codigo de vestimenta nadie usa el color blanco",
        containerColor = Color(0xFFF4F1EA),
        contentColor = Color(0xFF4A4F55)
    )
}

@Composable
fun GraduacionSeccion(){
    Nuevo(
        tipoEventoInicial = "Graduacion",
        notasIniciales = "codigo de vestimenta colores azul y negro",
        containerColor = Color(0xFFF4F1EA),
        contentColor = Color(0xFF4A4F55)
    )
}



/* compartir evento dirigido a la pagina*/



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
    var anfitrion by remember { mutableStateOf("") }



    val scrollState = rememberScrollState()//desplazamiento

    var esImportante by remember { mutableStateOf(false) }
    var nuevaHora by remember { mutableStateOf("") }
    var nuevoTitulo by remember { mutableStateOf("") }
    val actividades = remember {
        mutableStateListOf<Actividad>()
    }


    Card(
        modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp), // Limitamos la altura
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
            // Campo: tipo
            OutlinedTextField(
                value = tipoEvento,
                onValueChange = { tipoEvento = it },
                label = { Text("Tipo de Evento") },
                modifier = Modifier.fillMaxWidth(),
                enabled = tipoEventoInicial.isEmpty() // Si viene de plantilla, lo bloqueamos
            )
            OutlinedTextField(
                value = anfitrion,
                onValueChange = { anfitrion = it },
                label = { Text("Anfitriones") },
                modifier = Modifier.fillMaxWidth()
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
                text = "Programa",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4A4F55),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nuevaHora,
                    onValueChange = { nuevaHora = it },
                    label = { Text("Hora") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = nuevoTitulo,
                    onValueChange = { nuevoTitulo = it },
                    label = { Text("Actividad") },
                    modifier = Modifier.weight(2f)
                )
            }
                //BOTONES DE EDICION Y BLOQUEO QUE DEFINEN LA IMPORTANCIA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        if (nuevaHora.isNotBlank() && nuevoTitulo.isNotBlank()) {
                            // AGREGAR A LA LISTA
                            actividades.add(Actividad(nuevaHora, nuevoTitulo,"",esImportante = esImportante))
                            nuevaHora = ""
                            nuevoTitulo = ""
                            esImportante = false
                        }
                    }) {
                        Text("+")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("¿Es importante?")
                    Checkbox(
                        checked = esImportante,
                        onCheckedChange = { esImportante = it }
                    )
                }



            Spacer(modifier = Modifier.height(24.dp))

            // generacion de linea del tiempo
            Text("Vista Previa del Programa:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (actividades.isEmpty()) {
                Text("No hay actividades aún...", color = Color.Gray, fontSize = 12.sp)
            } else {
                // ordena la linea del tiempo por hora
                val listaOrdenada = actividades.sortedBy { it.hora }

                listaOrdenada.forEachIndexed { index, actividad ->

                    FilaDeActividad(
                        actividad = actividad,
                        esElUltimo = index == listaOrdenada.size - 1 ,// Usar el tamaño de la ordenada
                        onBorrar = { actividades.remove(actividad) }
                    )
                }
            }

            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        guardarNuevoEvento(
                            titulo = nombre,        // Estado: nombre
                            anfitrion = anfitrion,  // Estado: anfitrion
                            descripcion = notas,    // Estado: notas
                            fecha = fecha,          // Estado: fecha
                            hora = nuevaHora.ifBlank { "00:00" },
                            lugar = ubicacion,      // Estado: ubicacion
                            programa = actividades.toList(),// Convertimos el estado a lista fija
                            onSuccess = {
                                // Limpiamos los campos o cerramos el formulario
                                nombre = ""
                                fecha = ""
                                ubicacion = ""
                                actividades.clear()
                                println("Evento guardado correctamente")
                            },
                            onError = { error ->
                                println("Error al guardar en Firebase: ${error.message}")
                            }
                        )
                    }
                },

                modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        disabledContainerColor = Color.Gray
                    ),
                    enabled = nombre.isNotBlank() // Se deshabilita si no hay nombre
                ) {
                Text("Confirmar y Guardar Evento", color = Color.White)
            }
        }
    }
}

@Composable //elementos de la linea del tiempo
fun FilaDeActividad(actividad: Actividad, esElUltimo: Boolean,onBorrar: () -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(30.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = if (actividad.esImportante) Color(0xFF2196F3) else Color.Gray,
                        shape = CircleShape
                    )
            )
            if (!esElUltimo) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.LightGray)
                )
            }
        }

        // --- 2. COLUMNA DEL TEXTO (CENTRO) ---
        Column(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 16.dp)
                .weight(1f) // Esto empuja al icono a la derecha
        ) {
            Text(text = actividad.hora, fontSize = 11.sp, color = Color.Gray)
            Text(text = actividad.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        // 3. ICONO DE BORRAR (Queda solo a la derecha)
        IconButton(
            onClick = onBorrar,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Borrar",
                tint = Color.Red
            )
        }
    }
}

fun guardarNuevoEvento(titulo: String,anfitrion: String, descripcion: String, fecha: String, hora: String, lugar: String, programa: List<Actividad>, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val creadorUid = auth.currentUser?.uid ?: return

    val nuevoEventoRef = db.collection("eventos").document()

    val evento = Evento(
        id = nuevoEventoRef.id,
        titulo = titulo,
        anfitrion = anfitrion,
        descripcion = descripcion,
        fecha = fecha,
        hora = hora,
        lugar = lugar,
        creadorId = creadorUid,
        programa = programa // Se guarda la lista completa aquí
    )

    nuevoEventoRef.set(evento)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { excepcion -> onError(excepcion) }
}

@Composable
fun MenuBoton(){
    Card(modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding(), colors = CardDefaults.cardColors(containerColor = Color.Black/*(0xFF2196F3)*/), shape = androidx.compose.ui.graphics.RectangleShape) {
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
                    text = "Generar QR",
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

