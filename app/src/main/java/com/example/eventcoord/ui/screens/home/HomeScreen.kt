package com.example.eventcoord.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventcoord.R
import com.example.eventcoord.ui.viewmodels.EventosViewModel
import com.example.eventcoord.ui.base64ToImageBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onEvent: (String) -> Unit, onNewevent: () -> Unit, onProfile: () -> Unit, viewModel: EventosViewModel = viewModel()) {
    val logo = painterResource(R.drawable.eventcoord_logo)
    val usuario = painterResource(R.drawable.usuario)
    val presentacion = painterResource(R.drawable.eventcoord_logo_presentacion) // Imagen por defecto

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    var profileImageBase64 by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarEventos()

        currentUser?.uid?.let { uid ->
            db.collection("administradores").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()){
                        profileImageBase64 = document.getString("fotoPerfil") ?: ""
                    }
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onProfile,
                        modifier = Modifier.size(70.dp).padding(8.dp)
                    ) {
                        if (profileImageBase64.isNotEmpty() && base64ToImageBitmap(profileImageBase64) != null) {
                            Image(
                                bitmap = base64ToImageBitmap(profileImageBase64)!!,
                                contentDescription = "Ir al perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        } else {
                            Image(
                                painter = usuario,
                                contentDescription = "Ir al perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "MI CUENTA",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = logo,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(60.dp)
                    )
                }
            },
            bottomBar = {
                Card(modifier = Modifier.fillMaxWidth().navigationBarsPadding(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = androidx.compose.ui.graphics.RectangleShape) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onNewevent
                        ) {
                            Text(
                                text = "Nuevo Evento",
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "PRÓXIMOS EVENTOS",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 24.dp) // Un poco más de aire
                    )

                    if (viewModel.listaEventos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(205.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aún no tienes eventos.\n¡Crea uno nuevo!",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        HorizontalMultiBrowseCarousel(
                            state = rememberCarouselState { viewModel.listaEventos.count() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            preferredItemWidth = 200.dp, // Ligeramente más anchas para que el texto quepa mejor
                            itemSpacing = 12.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) { index ->
                            val eventoReal = viewModel.listaEventos[index]
                            val bitmapPortada = eventoReal.portada.takeIf { it.isNotBlank() }?.let { base64ToImageBitmap(it) }

                            Box(
                                modifier = Modifier
                                    .clickable { onEvent(eventoReal.id) }
                                    .height(260.dp) // Hice la tarjeta un poco más alta para que luzca la foto
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                            ) {
                                // 1. LA FOTO DE FONDO
                                if (bitmapPortada != null) {
                                    Image(
                                        bitmap = bitmapPortada,
                                        contentDescription = eventoReal.titulo,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Image(
                                        painter = presentacion,
                                        contentDescription = eventoReal.titulo,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                // 2. EL DEGRADADO Y TÍTULO SOBREPUESTO (OVERLAY)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter) // Se ancla abajo
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                            )
                                        )
                                        .padding(top = 24.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
                                ) {
                                    Text(
                                        text = eventoReal.titulo,
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2, // Si el título es muy largo, usa 2 líneas
                                        overflow = TextOverflow.Ellipsis // Y si no cabe, pone "..."
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}