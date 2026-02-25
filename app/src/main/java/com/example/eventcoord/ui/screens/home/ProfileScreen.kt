package com.example.eventcoord.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventcoord.R
import com.example.eventcoord.ui.components.LoadingOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape // Necesario para el recorte circular
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ProfileScreen(onLogOut: () -> Unit, onBackClick: () -> Unit){
    val usuario = painterResource(R.drawable.usuario)
    val scope = rememberCoroutineScope() // Ejecuta la espera de tiempo
    var isLoading by remember { mutableStateOf(false) } // Controla si se ve la carga
    var actualSection by remember { mutableStateOf("Perfil") } // Controla que se muestra (Perfil/Configuracion)
    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background,
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
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (actualSection == "Perfil") "Mi Perfil" else "Mi Configuración",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            bottomBar= {
                Card(modifier = Modifier.fillMaxWidth().navigationBarsPadding(), colors = CardDefaults.cardColors(containerColor = Color.Black), shape = androidx.compose.ui.graphics.RectangleShape) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { actualSection = "Perfil"}
                        ) {
                            Text(
                                text = "Perfil",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = if(actualSection == "Perfil") Color.White else Color.Gray
                            )
                        }
                        Spacer(modifier= Modifier.width(8.dp))
                        TextButton(
                            onClick = { actualSection = "Configuracion"}
                        ) {
                            Text(
                                text = "Configuración",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = if(actualSection == "Configuracion") Color.White else Color.Gray
                            )
                        }
                    }
                }
            }
        ){ innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize() //
                    .padding(innerPadding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                when (actualSection) {
                    "Perfil" -> {
                        Image(
                            painter = usuario,
                            contentDescription = "Foto de usuario",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Aquí irán todos tus datos registrados", textAlign = TextAlign.Center)
                    }
                    "Configuracion" -> {
                        Text("Opciones de la aplicación", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(32.dp))
                        TextButton( // Botón para Cerrar Sesion
                            onClick = {
                                scope.launch {
                                    isLoading = true // Activa la carga
                                    delay(2000) // Espera 2 segundos
                                    isLoading = false // Desactica la carga
                                    onLogOut() // Cierra la sesion
                                }
                            },
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "Cerrar Sesión",
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Center,
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
        LoadingOverlay(isLoading = isLoading) // Coloca el Overlay por encima de lo demas
    }
}