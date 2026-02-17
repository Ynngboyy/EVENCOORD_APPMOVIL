package com.example.eventcoord.ui.screens.home

import android.net.sip.SipProfile
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.buildSpannedString
import com.example.eventcoord.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onBackClick: () -> Unit, onEvent: () -> Unit, onNewevent: () -> Unit, onProfile: () -> Unit) {
    val logogris = painterResource(R.drawable.eventcoord_logo_gris)
    val usuario = painterResource(R.drawable.usuario)
    Scaffold(modifier = Modifier.fillMaxSize().statusBarsPadding()/*marca un espacio entre las notificaciones del sistema y la informacion de la aplicacion*/,
        bottomBar={Card( // crea la barra con los text button para la navegacion del usuario
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.LightGray
            )
        ) {
            var isVisible by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                TextButton(
                    onClick = onEvent
                ) {
                    Text("EVENTOS")
                    //color = Color.Black
                }
                Spacer(modifier = Modifier.width(5.dp))
                TextButton(
                    onClick = onNewevent
                ) { Text("NUEVO EVENTO") }

                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "INVITACION"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    onClick = onBackClick,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = Color.White
                    )

                }
            }
        }
            /*AnimatedVisibility(visible = isVisible) {
                Box(
                    modifier = Modifier
                        .size(400.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ){
                    Text("PRUEBA")
                }
            }*/
        }

        ) { innerPadding ->
        Box( //forma la barra superior que contiene el logo y nombre de la aplicacion
            modifier = Modifier
                .fillMaxSize()
        )
        {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton( //crea el boton para la configuracion del usuario
                    onClick = onProfile,
                    modifier = Modifier.size(70.dp).padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.usuario),
                        contentDescription = "Perfil de usuario",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )

                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "MI CUENTA",
                    color = Color.Black,
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
                Row() {
                    Text(
                        text = "PROXIMOS EVENTOS"
                    )
                }
                Row() {
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
                            CarouselItem(3, R.drawable.eventcoord_logo_gris, "image"),
                           // CarouselItem(4, R.drawable.eventcoord_logo_gris, "imagen"),
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