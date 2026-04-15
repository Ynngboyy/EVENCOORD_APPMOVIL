package com.example.eventcoord.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventcoord.R
import com.example.eventcoord.ui.components.LoadingOverlay
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PasswordScreen(onBackClick: () -> Unit) {
    // VARAIBLES DE ESTADO
    val logogris = painterResource(R.drawable.eventcoord_logo_v2) // Imagen de Logo
    var isLoading by remember { mutableStateOf(false)} // Variable para guardar el email
    var isError by remember { mutableStateOf(false) }
    // NOTIFICACIONES
    val testNotification = remember { mutableStateOf(false)} // Notificacion de correo enviado
    val notificationProblem = remember { mutableStateOf(false)} // Correo erroneo o faltante
    val notificationEmail = remember { mutableStateOf(false)} // Correo erroneo o faltante
    // VARIABLES DE USUARIO
    var email by remember { mutableStateOf("")} // Variable para guardar el email
    Box(modifier = Modifier.fillMaxSize()){
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)// Padding del Scafold
                    .padding(32.dp) // Margen para que no choque con los bordes
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = logogris,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(220.dp)
                        .height(220.dp)
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.height(32.dp)) // Espacios para una mejor presentacion
                Text(
                    text = "Recuperar contraseña",
                    fontSize = 32.sp,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                Text(
                    text = "Para recuperar su contraseña ingrese el correo vinculado a su cuenta",
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                TextField(
                    value = email,
                    onValueChange = { email = it
                        isError = false }, // Actualiza la variable cuando el usuario escribe
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),
                    isError = isError
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ){
                    Button( // Botón para Recuperar contraseña
                        onClick = {
                            if (email.isNotEmpty()) {
                                isLoading = true
                                val auth = FirebaseAuth.getInstance()
                                auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful){
                                            testNotification.value = true
                                        } else {
                                            isError = true
                                            notificationProblem.value = true
                                        }
                                }
                            } else {
                                isError = true
                                notificationEmail.value = true
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = !isLoading
                    ) {
                        Text("Recuperar")
                    }
                    Button( // Botón para Cancelar
                        onClick = onBackClick,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Cancelar")
                    }
                }
                if(testNotification.value) {
                    AlertDialog(
                        onDismissRequest = {
                            onBackClick()
                        },
                        title = {
                            Text(text = "Recuperacion de contraseña")
                        },
                        text = {
                            Text(text = "Se ha enviado un correo para recuperar su contraseña")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onBackClick()
                                }
                            ) {
                                Text("Volver")
                            }
                        }
                    )
                }
                if(notificationProblem.value) {
                    AlertDialog(
                        onDismissRequest = {
                            notificationProblem.value = false
                        },
                        title = {
                            Text(text = "Error")
                        },
                        text = {
                            Text(text = "Correo electronico invalido")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    notificationProblem.value = false
                                }
                            ) {
                                Text("Volver")
                            }
                        }
                    )
                }
                if(notificationEmail.value) {
                    AlertDialog(
                        onDismissRequest = {
                            notificationEmail.value = false
                        },
                        title = {
                            Text(text = "Recuperacion de contraseña")
                        },
                        text = {
                            Text(text = "Asegurese de llenar los campos")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    notificationEmail.value = false
                                }
                            ) {
                                Text("Volver")
                            }
                        }
                    )
                }
            }
        }
        LoadingOverlay(isLoading = isLoading) // Coloca el Overlay por encima de lo demas
    }
}