package com.example.eventcoord.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.lang.reflect.Field

@Composable
fun EventScreen(onBackClick: () -> Unit) {
    var actualSection by remember { mutableStateOf("Principal") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Título dinámico basado en la sección
            val topBarTitle = if (actualSection == "Principal") "Volver" else actualSection

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (actualSection == "Principal") onBackClick() else actualSection = "Principal"
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
        bottomBar = {
            BottomMenu(onPrincipalClick = { actualSection = "Principal" })
        }
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
                    Text(
                        text = "Su evento !nombreevento!",
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Usamos el componente reutilizable
                    MenuCard("Descripción del evento", Color(0xFF4A4F55), Color(0xFFF4F1EA)) {
                        actualSection = "Descripcion"
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    MenuCard("Programa", Color(0xFFF4F1EA), Color(0xFF4A4F55)) {
                        actualSection = "Programa"
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    MenuCard("Galeria", Color(0xFF243F63), Color(0xFFF4F1EA)) {
                        actualSection = "Galeria"
                    }
                }

                // Secciones de card
                "Descripcion" -> DescripcionSeccion()
                "Programa" -> ProgramaSeccion()
                "Galeria" -> GaleriaSeccion()
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

@Composable //permite modificar detalles esteticos como colores, tamaño y tipo
fun DetailView(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Center,

    )
}

@Composable //contenido de la seccion descripcion
fun DescripcionSeccion (){
    var invitados by remember { mutableStateOf("")}
    Column(
        modifier = Modifier.fillMaxSize().padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
           modifier = Modifier
               .fillMaxWidth()
               .height(100.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4A4F55))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text( text = "Acerca del Evento", textAlign = TextAlign.Center,style = MaterialTheme.typography.headlineMedium, color = Color(0xFFF4F1EA))
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        Text("Este evento se realizará en el salón UTTEC")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Contando con la siguiente cantidad de invitados")
        Spacer(modifier = Modifier.height(8.dp))


    }
}

@Composable
fun ProgramaSeccion(){
    Column(
        modifier = Modifier.fillMaxSize().padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4A4F55))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text( text = "Programa", textAlign = TextAlign.Center,style = MaterialTheme.typography.headlineMedium, color = Color(0xFFF4F1EA))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Text("Este evento se realizará en el salón UTTEC")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Contando con la siguiente cantidad de invitados")
        Spacer(modifier = Modifier.height(8.dp))


    }
}

@Composable
fun GaleriaSeccion(){
    Column(
        modifier = Modifier.fillMaxSize().padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4A4F55))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text( text = "Galeria", textAlign = TextAlign.Center,style = MaterialTheme.typography.headlineMedium, color = Color(0xFFF4F1EA))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))


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
                Text("Editar", color = Color(0xFF4A4F55))
            }
        }
    }
}