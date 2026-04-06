@file:Suppress("LABEL_NAME_CLASH")

package com.example.eventcoord.ui.screens.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.eventcoord.data.model.Actividad
import com.example.eventcoord.data.model.Evento
import com.example.eventcoord.ui.viewmodels.EventDetailViewModel
import com.example.eventcoord.ui.viewmodels.FotoItem
import com.example.eventcoord.ui.viewmodels.GaleriaViewModel
import com.example.eventcoord.ui.viewmodels.ReproductorViewModel
import com.example.eventcoord.ui.viewmodels.VideoViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
fun EventScreen(onBackClick: () -> Unit, eventoId: String, viewModel: EventDetailViewModel = viewModel(),reproductorViewModel: ReproductorViewModel = viewModel(),videoViewModel: VideoViewModel = viewModel()) {
    var actualSection by remember { mutableStateOf("Principal") }
    LaunchedEffect(eventoId) {
        viewModel.cargarEventoPorId(eventoId)


        videoViewModel.actualizarFotosExistentes(eventoId)
    }
    val eventoCargado = viewModel.eventoCargado
    if (eventoCargado == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {

        val videosState by produceState<List<String>>(initialValue = emptyList()) {
            try {
                FirebaseFirestore.getInstance()
                    .collection("favoritos")
                    // Solo videos de este evento
                    .whereEqualTo("eventoId", eventoId)
                    .snapshots()
                    .map { querySnapshot ->
                        querySnapshot.documents.mapNotNull { it.getString("videoUrl") }
                    }
                    .collect { value = it }
            } catch (e: Exception) {
                Log.e("JDCAR_DEBUG", "ERROR DE SALIDA: ${e.message}")
            }
        }

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
                                Spacer(modifier = Modifier.height(12.dp))
                                MenuCard("Momentos del evento", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurface) {
                                    actualSection = "Reproductor"
                                }
                            }
                        }
                        "Descripcion" -> DescripcionSeccion(evento = eventoCargado)
                        "Programa" -> ProgramaSeccion(actividades = eventoCargado.programa)
                        "Galeria" -> GaleriaSeccion(eventoId = eventoCargado.id)
                        "Favoritos" -> FavoritosSeccion(eventoId = eventoCargado.id)
                        "AccesoQR" -> AccesoQR(eventoId = eventoCargado.id)
                        "Reproductor" -> GaleriaVideosScreen(reproductorVM = reproductorViewModel, videosFromFirebase = videosState)
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

    val grosorBorde = (Math.abs(offsetX.value) / 60f).coerceIn(0f, 4f).dp

    val colorBorde = when {
        offsetX.value > 10 -> Color.Green.copy(alpha = (offsetX.value / 300f).coerceIn(0f, 0.9f))
        offsetX.value < -10 -> Color.Red.copy(alpha = (-offsetX.value / 300f).coerceIn(0f, 0.9f))
        else -> Color.Transparent
    }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer { rotationZ = offsetX.value / 25f // Rotación suave

                val scale = (1f - (Math.abs(offsetX.value) / 3000f)).coerceIn(0.9f, 1f)
                scaleX = scale
                scaleY = scale}
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
        // Fondo negro
        border = BorderStroke(width = grosorBorde, color = colorBorde),
        colors = CardDefaults.cardColors(containerColor = Color.Black)

    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
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
fun FavoritosSeccion(

    eventoId: String,
    vModel: GaleriaViewModel = viewModel(),
    // Inyectamos el motor de video que creamos
    videoViewModel: VideoViewModel = viewModel()
) {
    val listaFavoritos by vModel.favoritos.collectAsState()
    LaunchedEffect(eventoId) { vModel.cargarFavoritos(eventoId) }

    var fotoSeleccionada by remember { mutableStateOf<String?>(null) }

    // Estados del video
    val procesando = videoViewModel.isProcessing
    val progreso = videoViewModel.progress
    val rutaVideo by remember { mutableStateOf<String?>(null) }
    //notificacion del video
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(procesando) {
        if (!procesando && progreso >= 1f) {
            snackbarHostState.showSnackbar(
                message = "¡Video generado con éxito!",
                actionLabel = "Ver ahora",
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent // Mantiene el fondo
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(8.dp)) {
            // Encabezado de la sección
            Card(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Mis Fotos Guardadas",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //SECCIÓN DE GENERACIÓN DE VIDEO
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (procesando) {
                        Text("Procesando lote de 10 fotos...", fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { progreso }, // <--- Usa llaves { } para pasarlo como función
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(CircleShape)
                        )
                        Text("${(progreso * 100).toInt()}%")
                    } else {
                        Button(
                            onClick = {
                                // Solo envíale el ID del evento
                                videoViewModel.processNextBatch(eventoId)
                            },
                            enabled = listaFavoritos.size >= 10,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VideoLibrary, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Generar Video (Siguiente Lote)")
                        }
                    }
                }
            }


            if (listaFavoritos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Aún no has guardado fotos.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(listaFavoritos) { foto ->
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { fotoSeleccionada = foto.urlImagen },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            AsyncImage(
                                model = foto.urlImagen,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }

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


@Composable
fun Reproductor(url: String, onDismiss: () -> Unit) {
    val context = LocalContext.current

    // Inicia ExoPlayer una sola vez
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true // Auto-play
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Interfaz del reproductor
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true // Muestra pausa, play y barra de tiempo
                    setBackgroundColor(0xFF000000.toInt())
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Botón para cerrar
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
        }
    }
}

@Composable
fun GaleriaVideosScreen(
    reproductorVM: ReproductorViewModel,
    videosFromFirebase: List<String>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center // Para centrar texto
    ) {
        if (videosFromFirebase.isEmpty()) {

            // ESTADO VACÍO: Si la lista llega de Firestore en 0
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary, // Necesitas importar Icons.Default
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Aún no hay videos generados para este evento.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

        } else {

            // CUADRÍCULA DE VIDEOS (Estilo Instagram)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(videosFromFirebase) { url ->
                    VideoGridItem(
                        url = url,
                        onClick = { reproductorVM.seleccionarVideo(url) }
                    )
                }
            }
        }

        // REPRODUCTOR EMERGENTE (Overlay con Dialog)
        reproductorVM.videoActivoUrl?.let { urlActiva ->
            Dialog(
                onDismissRequest = { reproductorVM.cerrarReproductor() },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false, // Esto permite pantalla completa real
                    dismissOnBackPress = true
                )
            ) {

                Reproductor(
                    url = urlActiva,
                    onDismiss = { reproductorVM.cerrarReproductor() }
                )
            }
        }
    }
}

@Composable
fun VideoGridItem(url: String, onClick: () -> Unit) {

    val thumbnailUrl = remember(url) { url.replace(".mp4", ".jpg") }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RectangleShape)
            .clickable { onClick() }
            .background(Color.DarkGray)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumbnailUrl)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = "Miniatura de video",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.Gray.copy(alpha = 0.5f)),
            error = ColorPainter(Color.Red.copy(alpha = 0.1f))
        )

        // Capa para que el icono sea visible
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)),
                        startY = 100f
                    )
                )
        )

        // Icono de Play
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd) // Esquina inferior derecha
                .padding(8.dp)
                .size(24.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .padding(4.dp)
        )
    }
}
