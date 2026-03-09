package com.example.eventcoord.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventcoord.R
import com.example.eventcoord.ui.base64ToImageBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onEvent: () -> Unit, onNewevent: () -> Unit, onProfile: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val logo = painterResource(R.drawable.eventcoord_logo_gris)
    val usuario = painterResource(R.drawable.usuario) // La imagen por defecto
    var profileImageBase64 by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            db.collection("administradores").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()){
                        profileImageBase64 = document.getString("fotoPerfil") ?: ""
                    }
                }
                .addOnFailureListener {}
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background,
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
                                    .border(2.dp, color = Color(0xFF243F63), CircleShape)
                            )
                        } else {
                            Image(
                                painter = usuario,
                                contentDescription = "Ir al perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .border(2.dp, color = Color(0xFF243F63), CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "MI CUENTA",
                        color = Color(0xFF7A7F86),
                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.width(100.dp))
                    Image(
                        painter = logo,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(60.dp)
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
                        TextButton(
                            onClick = onNewevent
                        ) {
                            Text(
                                text = "Nuevo Evento",
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
            Box( //forma la barra superior que contiene el logo y nombre de la aplicacion
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column( //contiene y organiza los elementos que se visualizarán
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row{
                        Text(
                            text = "PROXIMOS EVENTOS"

                        )
                    }
                    Row{
                        data class CarouselItem( //se crea la clase donde se almacenara los valores para el carrusel
                            val id: Int,
                            @DrawableRes val imageResId: Int,
                            val contentDescription: String
                        )
                        val items = remember {// se declaran las imagenes que muestran en el carrusel
                            listOf(
                                CarouselItem(0, R.drawable.eventcoord_logo_presentacion, "im"),
                                CarouselItem(1, R.drawable.eventcoord_logo_presentacion, "ima"),
                                CarouselItem(2, R.drawable.eventcoord_logo_presentacion, "imag"),
                                CarouselItem(3, R.drawable.eventcoord_logo_presentacion, "image"), // CarouselItem(4, R.drawable.eventcoord_logo_gris, "imagen"),
                            )
                        }
                        HorizontalMultiBrowseCarousel( // se crea el carrusel de imagenes recuperadas de los valores ya declarados
                            state = rememberCarouselState { items.count() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 16.dp, bottom = 16.dp),
                            preferredItemWidth = 186.dp,
                            itemSpacing = 8.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) { i ->
                            val item = items[i]
                            Image(
                                modifier = Modifier
                                    .clickable( onClick = onEvent)//habilita la funcion de dar click y redirigirnos a la pestaña de eventos
                                    .height(205.dp)
                                    .maskClip(MaterialTheme.shapes.extraLarge),
                                painter = painterResource(id = item.imageResId),
                                contentDescription = item.contentDescription,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}