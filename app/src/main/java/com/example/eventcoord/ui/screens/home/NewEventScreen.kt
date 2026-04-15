package com.example.eventcoord.ui.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventcoord.data.model.Actividad
import com.example.eventcoord.data.model.Evento
import com.example.eventcoord.ui.base64ToImageBitmap
import com.example.eventcoord.ui.uriToBase64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

// --- 1. MODELO DE PLANTILLA Y DATOS ---
data class EventTemplate(
    val nombre: String,
    val icon: ImageVector,
    val tipoEvento: String,
    val notas: String,
    val programaPredefinido: List<Actividad>
)

val listaPlantillas = listOf(
    EventTemplate(
        nombre = "Boda",
        icon = Icons.Default.Favorite,
        tipoEvento = "Boda",
        notas = "Código de vestimenta: Formal. Por favor, evitar el color blanco.",
        programaPredefinido = listOf(
            Actividad(hora = "14:00", titulo = "Ceremonia", esImportante = true),
            Actividad(hora = "16:00", titulo = "Recepción y Cóctel", esImportante = false),
            Actividad(hora = "17:00", titulo = "Banquete", esImportante = true),
            Actividad(hora = "19:00", titulo = "Primer Baile", esImportante = true),
            Actividad(hora = "20:00", titulo = "Apertura de Pista", esImportante = false)
        )
    ),
    EventTemplate(
        nombre = "XV años",
        icon = Icons.Default.Stars,
        tipoEvento = "XV Años",
        notas = "Código de vestimenta: Formal-casual.",
        programaPredefinido = listOf(
            Actividad(hora = "16:00", titulo = "Misa de Acción de Gracias", esImportante = true),
            Actividad(hora = "18:00", titulo = "Llegada al Salón", esImportante = false),
            Actividad(hora = "19:00", titulo = "Vals y Brindis", esImportante = true),
            Actividad(hora = "20:30", titulo = "Cena", esImportante = false)
        )
    ),
    EventTemplate(
        nombre = "Graduación",
        icon = Icons.Default.School,
        tipoEvento = "Graduación",
        notas = "Código de vestimenta: Gala.",
        programaPredefinido = listOf(
            Actividad(hora = "10:00", titulo = "Acto Académico", esImportante = true),
            Actividad(hora = "13:00", titulo = "Fotografía de Generación", esImportante = false),
            Actividad(hora = "21:00", titulo = "Fiesta y Cena", esImportante = true)
        )
    ),
    EventTemplate(
        nombre = "Cumpleaños",
        icon = Icons.Default.Cake,
        tipoEvento = "Fiesta de Cumpleaños",
        notas = "¡Ven con ganas de divertirte!",
        programaPredefinido = listOf(
            Actividad(hora = "15:00", titulo = "Llegada de invitados", esImportante = false),
            Actividad(hora = "16:30", titulo = "Show y Dinámicas", esImportante = false),
            Actividad(hora = "18:00", titulo = "Romper la piñata", esImportante = true),
            Actividad(hora = "19:00", titulo = "Partir el pastel", esImportante = true)
        )
    ),
    EventTemplate(
        nombre = "Baby Shower",
        icon = Icons.Default.ChildCare,
        tipoEvento = "Baby Shower",
        notas = "Regalos sugeridos: Pañales etapa 1 y 2.",
        programaPredefinido = listOf(
            Actividad(hora = "10:00", titulo = "Bienvenida y Desayuno", esImportante = false),
            Actividad(hora = "11:30", titulo = "Juegos y Dinámicas", esImportante = true),
            Actividad(hora = "13:00", titulo = "Apertura de Regalos", esImportante = true)
        )
    ),
    EventTemplate(
        nombre = "En Blanco",
        icon = Icons.Default.AddBox,
        tipoEvento = "",
        notas = "",
        programaPredefinido = emptyList()
    )
)

// --- 2. PANTALLA PRINCIPAL ---
@Composable
fun NewEventScreen(onBackClick: () -> Unit) {
    var plantillaSeleccionada by remember { mutableStateOf<EventTemplate?>(null) }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Crear Evento",
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "PLANTILLAS RÁPIDAS",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // CARRUSEL DE PLANTILLAS
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaPlantillas) { template ->
                    val isSelected = plantillaSeleccionada == template
                    TemplateCard(
                        template = template,
                        isSelected = isSelected,
                        onClick = { plantillaSeleccionada = template }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // MENSAJE INICIAL O FORMULARIO
            if (plantillaSeleccionada == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Selecciona una plantilla arriba\npara comenzar",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut()
                ) {
                    // Le pasamos la plantilla seleccionada al formulario
                    NuevoFormulario(template = plantillaSeleccionada!!)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TemplateCard(template: EventTemplate, isSelected: Boolean, onClick: () -> Unit) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .width(110.dp)
            .height(110.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = template.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = template.nombre,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// --- 3. EL FORMULARIO (NUEVO) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoFormulario(template: EventTemplate) {
    var nombre by remember { mutableStateOf("") }
    var tipoEvento by remember { mutableStateOf("") }
    var horaEvento by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var anfitrion by remember { mutableStateOf("") }

    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker2 by remember { mutableStateOf(false) }
    var esImportante by remember { mutableStateOf(false) }
    var nuevaHora by remember { mutableStateOf("") }
    var nuevoTitulo by remember { mutableStateOf("") }
    var portadaBase64 by remember { mutableStateOf("") }

    val actividades = remember { mutableStateListOf<Actividad>() }

    // ¡MAGIA!: Cuando la plantilla cambia, pre-llenamos todos los datos
    LaunchedEffect(template) {
        tipoEvento = template.tipoEvento
        notas = template.notas
        actividades.clear()
        actividades.addAll(template.programaPredefinido)
    }

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

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (template.nombre == "En Blanco") "Detalles del Evento" else "Configurar ${template.nombre}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- PORTADA ---
            Text(
                text = "Portada del Evento",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (portadaBase64.isNotEmpty() && base64ToImageBitmap(portadaBase64) != null) {
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
                        Icon(Icons.Default.AddAPhoto, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Toca para agregar una portada", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- CAMPOS DE TEXTO ---
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Evento") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent)
            )
            TextField(
                value = tipoEvento,
                onValueChange = { tipoEvento = it },
                label = { Text("Tipo de Evento") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent)
            )
            TextField(
                value = anfitrion,
                onValueChange = { anfitrion = it },
                label = { Text("Anfitríon (es)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent)
            )
            OutlinedTextField(
                value = fecha,
                onValueChange = { },
                label = { Text("Fecha del Evento") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.DateRange, "Seleccionar fecha") },
                interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect { if (it is androidx.compose.foundation.interaction.PressInteraction.Release) mostrarDatePicker = true }
                    }
                }
            )
            OutlinedTextField(
                value = horaEvento,
                onValueChange = { },
                label = { Text("Hora de inicio") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.AccessTime, "Seleccionar hora") },
                interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect { if (it is androidx.compose.foundation.interaction.PressInteraction.Release) mostrarTimePicker = true }
                    }
                }
            )
            TextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación del Evento") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent)
            )
            TextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- PROGRAMA ---
            Text("Programa", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = nuevaHora,
                    onValueChange = { },
                    label = { Text("Hora") },
                    modifier = Modifier.weight(1.5f),
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.AccessTime, null) },
                    interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect { if (it is androidx.compose.foundation.interaction.PressInteraction.Release) mostrarTimePicker2 = true }
                        }
                    }
                )
                TextField(
                    value = nuevoTitulo,
                    onValueChange = { nuevoTitulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.weight(2f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, disabledContainerColor = Color.Transparent)
                )
            }
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    if (nuevaHora.isNotBlank() && nuevoTitulo.isNotBlank()) {
                        actividades.add(Actividad(hora = nuevaHora, titulo = nuevoTitulo, esImportante = esImportante))
                        nuevaHora = ""; nuevoTitulo = ""; esImportante = false
                    }
                }) { Text("+") }
                Spacer(modifier = Modifier.width(8.dp))
                Text("¿Es importante?", color = MaterialTheme.colorScheme.onSurface)
                Checkbox(checked = esImportante, onCheckedChange = { esImportante = it })
            }

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
                        onEditar = { nuevaHora = actividad.hora; nuevoTitulo = actividad.titulo; esImportante = actividad.esImportante; actividades.remove(actividad) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        guardarNuevoEvento(
                            titulo = nombre, anfitrion = anfitrion, descripcion = notas, fecha = fecha,
                            hora = horaEvento.ifBlank { "00:00" }, lugar = ubicacion, portada = portadaBase64,
                            programa = actividades.toList(),
                            onSuccess = { nombre = ""; fecha = ""; ubicacion = ""; actividades.clear(); println("Evento guardado") },
                            onError = { error -> println("Error: ${error.message}") }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = nombre.isNotBlank()
            ) {
                Text("Confirmar y Guardar Evento", color = MaterialTheme.colorScheme.onPrimary)
            }

            // --- DIÁLOGOS DE TIEMPO Y FECHA ---
            if (mostrarDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { mostrarDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                fecha = formatter.format(Date(millis))
                            }
                            mostrarDatePicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = { TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") } }
                ) { DatePicker(state = datePickerState) }
            }
            if (mostrarTimePicker) {
                AlertDialog(
                    onDismissRequest = { mostrarTimePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            horaEvento = String.format("%02d:%02d", timePickerStatePrincipal.hour, timePickerStatePrincipal.minute)
                            mostrarTimePicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = { TextButton(onClick = { mostrarTimePicker = false }) { Text("Cancelar") } },
                    text = { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Selecciona la hora", modifier = Modifier.padding(bottom = 16.dp)); TimePicker(state = timePickerStatePrincipal) } }
                )
            }
            if (mostrarTimePicker2) {
                AlertDialog(
                    onDismissRequest = { mostrarTimePicker2 = false },
                    confirmButton = {
                        TextButton(onClick = {
                            nuevaHora = String.format("%02d:%02d", timePickerStatePrograma.hour, timePickerStatePrograma.minute)
                            mostrarTimePicker2 = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = { TextButton(onClick = { mostrarTimePicker2 = false }) { Text("Cancelar") } },
                    text = { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Selecciona la hora", modifier = Modifier.padding(bottom = 16.dp)); TimePicker(state = timePickerStatePrograma) } }
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
        val actividadConId = actividad.copy(id = actividadRef.id)
        batch.set(actividadRef, actividadConId)
    }
    // 3. Ejecutamos todo de un solo golpe
    batch.commit()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { excepcion -> onError(excepcion) }
}
