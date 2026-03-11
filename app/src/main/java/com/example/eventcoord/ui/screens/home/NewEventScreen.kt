package com.example.eventcoord.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import com.example.eventcoord.data.model.Actividad
import com.example.eventcoord.data.model.Evento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NewEventScreen(onBackClick: () -> Unit) {
    var actualSection by remember { mutableStateOf("Principal") }
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = topBarTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        bottomBar = {
            MenuBoton()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (actualSection){
                "Principal" ->{
                    Text("PLANTILLAS", color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        Plantillas("XV años", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant) {
                            actualSection = "XV años"
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Plantillas("Boda", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface) {
                            actualSection = "Boda"
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Plantillas("Graduación", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary) {
                            actualSection = "Graduacion"
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "NUEVO EVENTO", color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { isVisible = !isVisible}) {
                            Text("Abrir formulario")
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    AnimatedVisibility(visible = isVisible) {
                        Nuevo("","")
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
            Text(text = text, color = contentColor)
        }
    }
}

@Composable
fun XVseccion(){
    Nuevo(tipoEventoInicial = "XV Años", notasIniciales = "Código de vestimenta: Formal-casual")
}

@Composable
fun BodaSeccion(){
    Nuevo(tipoEventoInicial = "Boda", notasIniciales = "Código de vestimenta: Nadie usa el color blanco")
}

@Composable
fun GraduacionSeccion(){
    Nuevo(tipoEventoInicial = "Graduación", notasIniciales = "Código de vestimenta: Colores azul y negro")
}



/* compartir evento dirigido a la pagina*/



@Composable
fun Nuevo(
    tipoEventoInicial: String = "",
    notasIniciales: String = ""
) {
    var nombre by remember { mutableStateOf("") }
    var tipoEvento by remember { mutableStateOf(tipoEventoInicial) }
    var fecha by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf(notasIniciales) }
    var anfitrion by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    var esImportante by remember { mutableStateOf(false) }
    var nuevaHora by remember { mutableStateOf("") }
    var nuevoTitulo by remember { mutableStateOf("") }
    val actividades = remember { mutableStateListOf<Actividad>() }

    Card(
        modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // CAMBIO
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (tipoEventoInicial.isEmpty()) "Detalles del Nuevo Evento" else "Configurar Plantilla",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary // CAMBIO
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TextFields
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del evento") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tipoEvento, onValueChange = { tipoEvento = it }, label = { Text("Tipo de Evento") }, modifier = Modifier.fillMaxWidth(), enabled = tipoEventoInicial.isEmpty())
            OutlinedTextField(value = anfitrion, onValueChange = { anfitrion = it }, label = { Text("Anfitriones") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = fecha, onValueChange = { fecha = it }, label = { Text("Fecha (DD/MM/AAAA)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = ubicacion, onValueChange = { ubicacion = it }, label = { Text("Ubicación / Salón") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Notas/Programa") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Programa",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary, // CAMBIO
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(value = nuevaHora, onValueChange = { nuevaHora = it }, label = { Text("Hora") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = nuevoTitulo, onValueChange = { nuevoTitulo = it }, label = { Text("Actividad") }, modifier = Modifier.weight(2f))
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    if (nuevaHora.isNotBlank() && nuevoTitulo.isNotBlank()) {
                        actividades.add(Actividad(nuevaHora, nuevoTitulo,"",esImportante = esImportante))
                        nuevaHora = ""
                        nuevoTitulo = ""
                        esImportante = false
                    }
                }) {
                    Text("+")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("¿Es importante?", color = MaterialTheme.colorScheme.onSurface)
                Checkbox(checked = esImportante, onCheckedChange = { esImportante = it })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Vista Previa del Programa:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))

            if (actividades.isEmpty()) {
                Text("No hay actividades aún...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
            } else {
                val listaOrdenada = actividades.sortedBy { it.hora }
                listaOrdenada.forEachIndexed { index, actividad ->
                    FilaDeActividad(
                        actividad = actividad,
                        esElUltimo = index == listaOrdenada.size - 1,
                        onBorrar = { actividades.remove(actividad) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        guardarNuevoEvento(
                            titulo = nombre,
                            anfitrion = anfitrion,
                            descripcion = notas,
                            fecha = fecha,
                            hora = nuevaHora.ifBlank { "00:00" },
                            lugar = ubicacion,
                            programa = actividades.toList(),
                            onSuccess = {
                                nombre = ""; fecha = ""; ubicacion = ""; actividades.clear()
                                println("Evento guardado correctamente")
                            },
                            onError = { error -> println("Error: ${error.message}") }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ),
                enabled = nombre.isNotBlank()
            ) {
                Text("Confirmar y Guardar Evento", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun FilaDeActividad(actividad: Actividad, esElUltimo: Boolean, onBorrar: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(30.dp)) {
            Box(
                modifier = Modifier.size(10.dp).background(
                    color = if (actividad.esImportante) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    shape = CircleShape
                )
            )
            if (!esElUltimo) {
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))
            }
        }
        Column(modifier = Modifier.padding(start = 8.dp, bottom = 16.dp).weight(1f)) {
            Text(text = actividad.hora, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            Text(text = actividad.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        IconButton(onClick = onBorrar, modifier = Modifier.padding(start = 8.dp)) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Borrar",
                tint = MaterialTheme.colorScheme.error
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
        programa = programa
    )

    nuevoEventoRef.set(evento)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { excepcion -> onError(excepcion) }
}

@Composable
fun MenuBoton(){
    Card(modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = androidx.compose.ui.graphics.RectangleShape) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
        ) {
            TextButton(onClick = { /* Lógica del QR */ }) {
                Text(
                    text = "Generar QR",
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}