package com.example.eventcoord.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.eventcoord.R
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.eventcoord.ui.components.LoadingOverlay

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onForgotPassword: () -> Unit, onRegister: () -> Unit) {
    val logogris = painterResource(R.drawable.eventcoord_logo_gris) // Imagen del logo
    val scope = rememberCoroutineScope() // Ejecuta la espera de tiempo
    var isLoading by remember { mutableStateOf(false) } // Controla si se ve la carga
    var email by remember { mutableStateOf("") } // Guardamos lo que el usuario escribe
    var password by remember { mutableStateOf("") } // Guardamos lo que el usuario escribe
    var hide by remember { mutableStateOf(true) } // Mostrar/Ocultar contraseña
    var isRemember by remember { mutableStateOf(false) } // Recordar usuario
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column( // Apilamos para organizar los elementos de forma vertical
                modifier = Modifier
                    .fillMaxSize() //
                    .padding(innerPadding) // Padding del Scafold
                    .padding(32.dp), // Margen para que no choque con los bordes
                horizontalAlignment = Alignment.CenterHorizontally, // Centra el contenido
                verticalArrangement = Arrangement.Center // Centra el contenido
            ) {
                Image(
                    painter = logogris,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(280.dp)
                        .height(280.dp)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp)) // Espacios para una mejor presentacion
                TextField(
                    value = email,
                    onValueChange = { email = it }, // Actualiza la variable cuando el usuario escribe
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    visualTransformation = if (hide) PasswordVisualTransformation() else VisualTransformation.None, // Condicion para Mostras/Ocultar contraseña
                    trailingIcon = {
                        IconButton(onClick = { hide = !hide }) {
                            Icon(
                                imageVector = if (hide) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (hide) "Mostrar Contraseña" else "Ocultar Contraseña"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Separa boton de checkbox y texto
                ) {
                    Row( //CheckBox y texto clickeable
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { isRemember = !isRemember } // Texto clickeable
                            .padding(vertical = 8.dp)
                    ) {
                        Checkbox(
                            checked = isRemember,
                            onCheckedChange = null
                        )
                        Text(
                            text = "Recordarme",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    TextButton(
                        onClick = onForgotPassword
                    ) {
                        Text(
                            text = "Olvidaste tu contraseña?",
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button( // Botón de Ingreso
                    onClick = {
                        scope.launch {
                            isLoading = true // Activa la carga
                            delay(2000) // Espera 2 segundos
                            isLoading = false // Desactica la carga
                            onLoginSuccess() // Se dirige a Home
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading // Desactiva el boton si ya esta cargando
                ) {
                    Text("Iniciar Sesión")
                }
                Spacer(modifier = Modifier.height(32.dp)) // Espacios para una mejor presentacion
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ){
                    Text (
                        text = "No tienes una cuenta?",
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    TextButton(
                        onClick = onRegister
                    ) {
                        Text(
                            text = "Crea una",
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                }
            }
        }
        LoadingOverlay(isLoading = isLoading) // Coloca el Overlay por encima de lo demas
    }
}