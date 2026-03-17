package com.example.eventcoord.ui.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import com.example.eventcoord.data.model.Actividad
import com.example.eventcoord.data.model.Evento
import com.example.eventcoord.ui.base64ToImageBitmap
import com.example.eventcoord.ui.uriToBase64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Nuevo(
    tipoEventoInicial: String = "",
    notasIniciales: String = ""
) {
    var nombre by remember { mutableStateOf("") }
    var tipoEvento by remember { mutableStateOf(tipoEventoInicial) }
    var horaEvento by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf(notasIniciales) }
    var anfitrion by remember { mutableStateOf("") }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker2 by remember { mutableStateOf(false) }
    var esImportante by remember { mutableStateOf(false) }
    var nuevaHora by remember { mutableStateOf("") }
    var nuevoTitulo by remember { mutableStateOf("") }
    var portadaBase64 by remember { mutableStateOf("") }
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                portadaBase64 = uriToBase64(context, uri) ?: ""
            }
        }
    )
    val datePickerState = rememberDatePickerState()
    val timePickerStatePrincipal = rememberTimePickerState()
    val timePickerStatePrograma = rememberTimePickerState()
    val scrollState = rememberScrollState()
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
            // --- SECCIÓN DE PORTADA DEL EVENTO ---
            Text(
                text = "Portada del Evento",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Altura para una portada elegante
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant) // Fondo gris claro si no hay foto
                    .clickable {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (portadaBase64.isNotEmpty() && base64ToImageBitmap(portadaBase64) != null) {
                    // --- VISTA PREVIA DE LA FOTO SELECCIONADA ---
                    Image(
                        bitmap = base64ToImageBitmap(portadaBase64)!!,
                        contentDescription = "Portada seleccionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Cambiar portada",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape).padding(8.dp)
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Subir portada",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toca para agregar una portada",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = nombre,
                onValueChange = { nombre = it }, // Actualiza la variable cuando el usuario escribe
                label = { Text("Nombre Evento") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
            )
            TextField(
                value = tipoEvento,
                onValueChange = { tipoEvento = it }, // Actualiza la variable cuando el usuario escribe
                label = { Text("Tipo de Evento") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                enabled = tipoEventoInicial.isEmpty()
            )
            TextField(
                value = anfitrion,
                onValueChange = { anfitrion = it }, // Actualiza la variable cuando el usuario escribe
                label = { Text("Anfitrion(es)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
            )
            OutlinedTextField(
                value = fecha,
                onValueChange = { },
                label = { Text("Fecha del Evento") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                },
                interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                mostrarDatePicker = true // Abre el calendario al tocar
                            }
                        }
                    }
                }
            )
            OutlinedTextField(
                value = horaEvento,
                onValueChange = { },
                label = { Text("Hora de inicio") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora")
                },
                interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                mostrarTimePicker = true
                            }
                        }
                    }
                }
            )
            TextField(
                value = ubicacion,
                onValueChange = { ubicacion = it }, // Actualiza la variable cuando el usuario escribe
                label = { Text("Ubicación del Evento") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                )
            )
            TextField(
                value = notas,
                onValueChange = { notas = it }, // Actualiza la variable cuando el usuario escribe
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                minLines = 3
            )
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
                OutlinedTextField(
                    value = nuevaHora,
                    onValueChange = { },
                    label = { Text("Hora") },
                    modifier = Modifier.weight(1.5f),
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.AccessTime, contentDescription = "Seleccionar hora")
                    },
                    interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                    mostrarTimePicker2 = true
                                }
                            }
                        }
                    }
                )
                TextField(
                    value = nuevoTitulo,
                    onValueChange = { nuevoTitulo = it }, // Actualiza la variable cuando el usuario escribe
                    label = { Text("Titulo") },
                    modifier = Modifier.weight(2f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )
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
                        onBorrar = { actividades.remove(actividad) },
                        onEditar = {
                            nuevaHora = actividad.hora
                            nuevoTitulo = actividad.titulo
                            esImportante = actividad.esImportante
                            actividades.remove(actividad)
                        }
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
                            hora = horaEvento.ifBlank { "00:00" },
                            lugar = ubicacion,
                            portada = portadaBase64,
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
            // --- DIÁLOGO DEL CALENDARIO (DATE PICKER) ---
            if (mostrarDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { mostrarDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                fecha = formatter.format(Date(millis + TimeZone.getDefault().rawOffset))
                            }
                            mostrarDatePicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            // --- DIÁLOGO DEL RELOJ (TIME PICKER) ---
            if (mostrarTimePicker) {
                AlertDialog(
                    onDismissRequest = { mostrarTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val horaFormateada = String.format("%02d:%02d", timePickerStatePrincipal.hour, timePickerStatePrincipal.minute)
                            horaEvento = horaFormateada
                            mostrarTimePicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarTimePicker = false }) { Text("Cancelar") }
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Selecciona la hora", modifier = Modifier.padding(bottom = 16.dp))
                            TimePicker(state = timePickerStatePrincipal)
                        }
                    }
                )
            }
            if (mostrarTimePicker2) {
                AlertDialog(
                    onDismissRequest = { mostrarTimePicker2 = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val horaFormateada = String.format("%02d:%02d", timePickerStatePrograma.hour, timePickerStatePrograma.minute)
                            nuevaHora = horaFormateada
                            mostrarTimePicker2 = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarTimePicker2 = false }) { Text("Cancelar") }
                    },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Selecciona la hora", modifier = Modifier.padding(bottom = 16.dp))
                            TimePicker(state = timePickerStatePrograma)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FilaDeActividad(actividad: Actividad, esElUltimo: Boolean, onBorrar: () -> Unit, onEditar: () -> Unit) {
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
        IconButton(onClick = onEditar) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onBorrar) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Borrar",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

fun guardarNuevoEvento(titulo: String, anfitrion: String, descripcion: String, fecha: String, hora: String, lugar: String, portada: String, programa: List<Actividad>, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val creadorUid = auth.currentUser?.uid ?: return
    val nuevoEventoRef = db.collection("eventos").document()
    // Iniciamos el "Lote" de Informacion
    val batch = db.batch()
    // 1. Preparamos el Documento del Evento
    val evento = Evento(
        id = nuevoEventoRef.id,
        titulo = titulo,
        anfitrion = anfitrion,
        descripcion = descripcion,
        fecha = fecha,
        hora = hora,
        lugar = lugar,
        creadorId = creadorUid,
        portada = portada
    )
    batch.set(nuevoEventoRef, evento)

    // 2. Preparamos la Subcolección del Programa
    programa.forEach { actividad ->
        val actividadRef = nuevoEventoRef.collection("programa").document()
        batch.set(actividadRef, actividad)
    }
    // 3. Ejecutamos todo de un solo golpe
    batch.commit()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { excepcion -> onError(excepcion) }
}
