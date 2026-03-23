package com.example.eventcoord.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eventcoord.data.model.Actividad
import com.example.eventcoord.ui.viewmodels.EventDetailViewModel
import com.example.eventcoord.data.model.Evento
import com.example.eventcoord.ui.viewmodels.FotoItem
import com.example.eventcoord.ui.viewmodels.GaleriaViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@SuppressLint("UseKtx")
fun generarCodigoQR(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        println("Error: ${e.message}")
    } as Bitmap?
}

@Composable
fun EventScreen(onBackClick: () -> Unit, eventoId: String, viewModel: EventDetailViewModel = viewModel()) {
    var actualSection by remember { mutableStateOf("Principal") }
    LaunchedEffect(eventoId) {
        viewModel.cargarEventoPorId(eventoId)
    }
    val eventoCargado = viewModel.eventoCargado
    if (eventoCargado == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                val topBarTitle = if (actualSection == "Principal") "Detalles" else "${eventoCargado.titulo} - $actualSection"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (actualSection == "Principal") onBackClick() else actualSection = "Principal" }) {
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
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            viewModel.eliminarEvento(eventoCargado.id){
                                onBackClick()
                            }
                        },
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Crossfade(
                    targetState = actualSection,
                    label = "animacion_secciones"
                ) { seccionActiva ->
                    when (seccionActiva) {
                        "Principal" -> {
                            val scrollState = rememberScrollState()
                            Column(
                                modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = eventoCargado.titulo,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Organizado por: ${eventoCargado.anfitrion}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Button(
                                    onClick = { actualSection = "AccesoQR" },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.QrCode, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generar Pase de Acceso", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        InfoRow(label = "📅 Fecha", value = eventoCargado.fecha)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        InfoRow(label = "⏰ Hora", value = eventoCargado.hora)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        InfoRow(label = "📍 Lugar", value = eventoCargado.lugar)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Acerca del evento",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                                Text(
                                    text = eventoCargado.descripcion.ifBlank { "Sin descripción adicional." },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                MenuCard("Programa", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant) {
                                    actualSection = "Programa"
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                MenuCard("Galería", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary) {
                                    actualSection = "Galeria"
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                MenuCard("Fotos Guardadas", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface) {
                                    actualSection = "Favoritos"
                                }
                            }
                        }
                        "Descripcion" -> DescripcionSeccion(evento = eventoCargado)
                        "Programa" -> ProgramaSeccion(actividades = eventoCargado.programa)
                        "Galeria" -> GaleriaSeccion(eventoId = eventoCargado.id)
                        "Favoritos" -> FavoritosSeccion(eventoId = eventoCargado.id)
                        "AccesoQR" -> AccesoQR(eventoId = eventoCargado.id)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCard(text: String, containerColor: Color, contentColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = text, color = contentColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DescripcionSeccion(evento: Evento) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = evento.titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Organizado por: ${evento.anfitrion}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "Fecha", value = evento.fecha)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(label = "Hora", value = evento.hora)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(label = "Lugar", value = evento.lugar)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Notas Adicionales",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = evento.descripcion.ifBlank { "No hay notas adicionales para este evento." },
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
            textAlign = TextAlign.Justify,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun ProgramaSeccion(actividades: List<Actividad>) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Programa de Actividades",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (actividades.isEmpty()) {
            Text("No se han registrado actividades para este evento.", color = MaterialTheme.colorScheme.onBackground)
        } else {
            val listaOrdenada = actividades.sortedBy { it.hora }
            listaOrdenada.forEachIndexed { index, actividad ->
                FilaDeActividadConsulta(
                    actividad = actividad,
                    esElUltimo = index == listaOrdenada.size - 1
                )
            }
        }
    }
}

@Composable
fun FilaDeActividadConsulta(actividad: Actividad, esElUltimo: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(30.dp)) {
            Box(modifier = Modifier.size(10.dp).background(
                color = if (actividad.esImportante) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                shape = CircleShape
            ))
            if (!esElUltimo) {
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)))
            }
        }
        Column(modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)) {
            Text(text = actividad.hora, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Text(text = actividad.titulo, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun GaleriaSeccion(eventoId: String, vModel: GaleriaViewModel = viewModel()){
    val fotos by vModel.fotos.collectAsState()
    LaunchedEffect(eventoId) { vModel.cargarFotosRevision(eventoId) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Galería de Evento", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (fotos.isEmpty()) {
                Text("No hay más fotos por ahora", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            } else {
                val fotoActual = fotos.last()
                TarjetaInteractiva(
                    foto = fotoActual,
                    onSwipeLeft = { vModel.rechazarFoto(eventoId, fotoActual) },
                    onSwipeRight = { vModel.aceptarFoto(eventoId, fotoActual) }
                )
            }
        }
    }
}

@Composable
fun TarjetaInteractiva(foto: FotoItem, onSwipeLeft: () -> Unit, onSwipeRight: () -> Unit) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val urlLimpia = remember(foto.urlImagen) { foto.urlImagen.trim() }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer { rotationZ = offsetX.value / 20 }
            .pointerInput(foto.id) {
                detectDragGestures(
                    onDragEnd = {
                        if (offsetX.value > 400f) {
                            scope.launch { offsetX.animateTo(1000f); onSwipeRight(); offsetX.snapTo(0f) }
                        } else if (offsetX.value < -400f) {
                            scope.launch { offsetX.animateTo(-1000f); onSwipeLeft(); offsetX.snapTo(0f) }
                        } else {
                            scope.launch { offsetX.animateTo(0f) }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount.x) }
                    }
                )
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        // Fondo negro para que los huecos de la imagen se vean elegantes
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        AsyncImage(
            model = coil.request.ImageRequest.Builder(LocalContext.current)
                .data(urlLimpia)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            // Fit en vez de Crop para ver la foto entera
            contentScale = ContentScale.Fit,
            onError = { error -> println("ERROR_COIL: ${error.result.throwable.message}") }
        )
    }
}

@Composable
fun FavoritosSeccion(eventoId: String, vModel: GaleriaViewModel = viewModel()) {
    val listaFavoritos by vModel.favoritos.collectAsState()
    LaunchedEffect(eventoId) { vModel.cargarFavoritos(eventoId) }
    // Controla qué foto está abierta en grande
    var fotoSeleccionada by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Mis Fotos Guardadas", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (listaFavoritos.isEmpty()) {
            Text("Aún no has guardado fotos.", modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.onBackground)
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaFavoritos) { foto ->
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            // Al hacer clic, guardamos la URL para abrirla en grande
                            .clickable { fotoSeleccionada = foto.urlImagen },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        // Aquí dejamos Crop para que la cuadrícula se vea ordenada
                        AsyncImage(model = foto.urlImagen, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
    // Si hay una foto seleccionada, mostramos el Visor de Pantalla Completa
    fotoSeleccionada?.let { url ->
        VisorImagenCompleta(url = url, onDismiss = { fotoSeleccionada = null })
    }
}

@Composable
fun AccesoQR(eventoId: String) {
    val qrBitmap = remember(eventoId) { generarCodigoQR(eventoId) }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Código de Acceso",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier.size(300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (qrBitmap != null) {
                    Image(bitmap = qrBitmap.asImageBitmap(), contentDescription = "QR", modifier = Modifier.size(250.dp))
                } else {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$label: ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = value, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
    }
}

@Composable
fun VisorImagenCompleta(url: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Ocupa toda la pantalla
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f)) // Fondo oscuro semitransparente
                .clickable { onDismiss() }, // Si tocan el fondo negro, se cierra
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = url,
                contentDescription = "Imagen Ampliada",
                contentScale = ContentScale.Fit, // ¡Muestra la foto completa sin recortes!
                modifier = Modifier.fillMaxSize()
            )
            // Botoncito de cerrar en la esquina
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        }
    }
}