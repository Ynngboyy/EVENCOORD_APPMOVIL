package com.example.eventcoord.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onEvent: () -> Unit, onNewevent: () -> Unit, onProfile: () -> Unit) {
    val logogris = painterResource(R.drawable.eventcoord_logo_gris)
    var isVisible by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.Black), shape = androidx.compose.ui.graphics.RectangleShape) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier= Modifier.width(5.dp))
                        TextButton(
                            onClick = onNewevent
                        ) {
                            Text(
                                text = "Nuevo Evento",
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier= Modifier.width(5.dp))
                        IconButton(
                            onClick = {},
                            modifier = Modifier.padding(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                tint = Color.Black
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding() // <--- El padding de estado va AQUÍ, dentro del contenido
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton( //crea el boton para la configuracion del usuario
                        onClick = onProfile,
                        modifier = Modifier.size(70.dp).padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.usuario),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "MI CUENTA",
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Spacer(modifier = Modifier.width(100.dp))
                    Image(
                        painter = logogris,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .padding(8.dp)
                    )
                }
                Column( //contine y organiza los elementos que se visualizaran
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
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
                                CarouselItem(0, R.drawable.eventcoord_logo_gris, "im"),
                                CarouselItem(1, R.drawable.eventcoord_logo_gris, "ima"),
                                CarouselItem(2, R.drawable.eventcoord_logo_gris, "imag"),
                                CarouselItem(3, R.drawable.eventcoord_logo_gris, "image"), // CarouselItem(4, R.drawable.eventcoord_logo_gris, "imagen"),
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