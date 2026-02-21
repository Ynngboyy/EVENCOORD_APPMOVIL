package com.example.eventcoord.ui.screens.home

import androidx.compose.foundation.Image
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

@Composable
fun ProfileScreen(onLogOut: () -> Unit, onBackClick: () -> Unit){
    val usuario = painterResource(R.drawable.usuario)
    val scope = rememberCoroutineScope() // Ejecuta la espera de tiempo
    var isLoading by remember { mutableStateOf(false) } // Controla si se ve la carga
    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background,
            bottomBar= {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.Black), shape = androidx.compose.ui.graphics.RectangleShape) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {}
                        ) {
                            Text(
                                text = "Editar Perfil",
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier= Modifier.width(8.dp))
                        TextButton(
                            onClick = {}
                        ) {
                            Text(
                                text = "Configuración",
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier= Modifier.width(8.dp))
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
                        Spacer(modifier= Modifier.width(5.dp))
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                tint = Color.White
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
                verticalArrangement = Arrangement.Center
            ) {
                Text("PERFIL")
                Spacer(modifier = Modifier.height(7.dp))
                Image(
                    painter = usuario,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(160.dp)
                        .height(160.dp)
                        .padding(8.dp)
                )
            }
        }
        LoadingOverlay(isLoading = isLoading) // Coloca el Overlay por encima de lo demas
    }
}