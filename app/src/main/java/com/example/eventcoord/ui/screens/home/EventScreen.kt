package com.example.eventcoord.ui.screens.home

import androidx.compose.animation.core.Animatable
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eventcoord.data.model.Actividad
import com.example.eventcoord.data.model.EventDetailViewModel
import com.example.eventcoord.data.model.Evento
import com.example.eventcoord.ui.viewmodels.FotoItem
import com.example.eventcoord.ui.viewmodels.GaleriaViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import android.graphics.Bitmap
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

//funcion para generar qr tomando el id del evento
fun generarCodigoQR(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        // Generamos una matriz de bits (512x512 px)
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        null
    }
}


@Composable
fun EventScreen(onBackClick: () -> Unit,eventoId: String,viewModel: EventDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var actualSection by remember { mutableStateOf("Principal") }
    LaunchedEffect(eventoId) {
        viewModel.cargarEventoPorId(eventoId)
    }

    // Obtenemos el estado del evento desde el ViewModel
    val eventoCargado = viewModel.eventoCargado

    // MANEJO DE ESTADO DE CARGA: Si el evento es nulo, mostramos carga
    if (eventoCargado == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF2196F3))
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                // Título dinámico basado en la sección
                val topBarTitle =
                    if (actualSection == "Principal") "Detalles" else "${eventoCargado.titulo} - $actualSection"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (actualSection == "Principal") onBackClick() else actualSection =
                            "Principal"
                    }) {
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
            /*bottomBar = {
            BottomMenu(onPrincipalClick = { actualSection = "Principal" })
        }*/
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (actualSection) {
                    "Principal" -> {
                        // Título del Evento
                        val scrollState = rememberScrollState()

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 1. Título y Anfitrión
                            Text(
                                text = eventoCargado.titulo,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2196F3),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Organizado por: ${eventoCargado.anfitrion}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(
                                onClick = { actualSection = "AccesoQR" },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp), // Altura estándar para botones de acción
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2196F3), // Azul vibrante
                                    contentColor = Color.White
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.QrCode, // Asegúrate de tener esta importación
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Generar Pase de Acceso",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            // 2. TARJETA DE DETALLES (Fecha, Hora, Lugar)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F1EA)),
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

                            // 3. NOTAS ADICIONALES
                            Text(
                                text = "Acerca del evento",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Text(
                                text = if (eventoCargado.descripcion.isBlank()) "Sin descripción adicional." else eventoCargado.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4A4F55),
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )


                            // TARJETAS DE NAVEGACIÓN DEBAJO
                            MenuCard("Programa", Color(0xFF4A4F55), Color(0xFFF4F1EA)) {
                                actualSection = "Programa"
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            MenuCard("Galería", Color(0xFF243F63), Color(0xFFF4F1EA)) {
                                actualSection = "Galeria"
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tarjeta para ver las fotos guardadas
                            MenuCard("Fotos Guardadas", Color(0xFF2196F3), Color.White) {
                                actualSection = "Favoritos"
                            }
                        }
                    }


                    // Navegación de secciones
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

@Composable //controla las caracteristicas de creacion de las cards
fun MenuCard(text: String, containerColor: Color, contentColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
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


@Composable //contenido de la seccion descripcion
fun DescripcionSeccion (evento: Evento){
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Encabezado con el nombre del evento
        Text(
            text = evento.titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3)
        )

        Text(
            text = "Organizado por: ${evento.anfitrion}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Tarjeta de Detalles Rápidos
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F1EA))
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

        // Sección de Notas / Descripción larga
        Text(
            text = "Notas Adicionales",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (evento.descripcion.isBlank()) "No hay notas adicionales para este evento." else evento.descripcion,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF4A4F55)
        )

        /*sección para confirmacion de invitados y asistentes*/
    }
}

@Composable
fun ProgramaSeccion(actividades: List<Actividad>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()) // Para poder bajar si hay muchas tareas
    ) {
        Text(
            text = "Programa de Actividades",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (actividades.isEmpty()) {
            Text("No se han registrado actividades para este evento.")
        } else {
            // Ordenamos por hora antes de mostrar
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
                color = if (actividad.esImportante) Color(0xFF2196F3) else Color.Gray,
                shape = CircleShape
            ))
            if (!esElUltimo) {
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray))
            }
        }
        Column(modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)) {
            Text(text = actividad.hora, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = actividad.titulo, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun GaleriaSeccion(eventoId: String,vModel: GaleriaViewModel = viewModel()){
    val fotos by vModel.fotos.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(eventoId) {
        vModel.cargarFotosRevision(eventoId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Galería de Evento", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (fotos.isEmpty()) {
                Text("No hay más fotos por ahora", color = Color.Gray)
            } else {
                // Mostramos siempre la última foto de la lista (la que está arriba)
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
    val urlLimpia = remember(foto.url) { foto.url.trim() }
    Card(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .graphicsLayer { rotationZ = offsetX.value / 20 } // Efecto de rotación al deslizar
            .pointerInput(foto.id) {
                detectDragGestures(
                    onDragEnd = {
                        if (offsetX.value > 400f) { // Umbral derecha
                            scope.launch {
                                offsetX.animateTo(1000f)
                                onSwipeRight()
                                offsetX.snapTo(0f) // Reset para la siguiente
                            }
                        } else if (offsetX.value < -400f) { // Umbral izquierda
                            scope.launch {
                                offsetX.animateTo(-1000f)
                                onSwipeLeft()
                                offsetX.snapTo(0f)
                            }
                        } else {
                            scope.launch { offsetX.animateTo(0f) } // Regresa al centro
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount.x) }
                    }
                )
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        val context = LocalContext.current
        val imageLoader = remember {
            ImageLoader.Builder(context)
                .crossfade(true)
                .build()
        }

        AsyncImage(
            model = urlLimpia,
            imageLoader = imageLoader, // Forzamos el uso del loader configurado
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            onError = { error ->
                println("ERROR_COIL: ${error.result.throwable.message}")
            }
        )
    }
}


@Composable
fun FavoritosSeccion(eventoId: String,vModel: GaleriaViewModel = viewModel()) {
    val listaFavoritos by vModel.favoritos.collectAsState()

    LaunchedEffect(eventoId) {
        vModel.cargarFavoritos(eventoId)
    }
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Encabezado
        Card(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF243F63))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Mis Fotos Guardadas", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (listaFavoritos.isEmpty()) {
            Text("Aún no has guardado fotos.", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Cuadrícula de fotos
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columnas
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaFavoritos) { foto ->
                    Card(
                        modifier = Modifier.aspectRatio(1f), // Cuadradas
                        shape = RoundedCornerShape(12.dp)
                    ) {
                       AsyncImage(
                            model = foto.url,
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

@Composable
fun AccesoQR(eventoId: String) {
    // se genera un codigo solo una vez
    val qrBitmap = remember(eventoId) {
        generarCodigoQR(eventoId)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Código de Acceso",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A4F55)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Contenedor del QR
        Card(
            modifier = Modifier.size(300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (qrBitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR",
                        modifier = Modifier.size(250.dp)
                    )
                } else {
                    // En caso de que falle la generación
                    CircularProgressIndicator()
                }
            }
        }
    }
}


@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$label: ", fontWeight = FontWeight.Bold, color = Color(0xFF4A4F55))
        Text(text = value, color = Color.DarkGray)
    }
}

@Composable //barra inferior
fun BottomMenu(onPrincipalClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onPrincipalClick) {
                Text("Principal", color = Color(0xFF4A4F55))
            }
            Spacer(modifier = Modifier.width(16.dp))
            TextButton(onClick = {  }) {
                Text("Favoritos", color = Color(0xFF4A4F55))
            }
        }
    }
}