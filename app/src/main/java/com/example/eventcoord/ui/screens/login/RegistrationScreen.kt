package com.example.eventcoord.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventcoord.R
import com.example.eventcoord.ui.components.LoadingOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(onBackClick: () -> Unit) {
    val logogris = painterResource(R.drawable.eventcoord_logo_gris) // Imagen de Logo
    val testNotification = remember { mutableStateOf(false)}
    val notificationProblem = remember { mutableStateOf(false)}
    val scrollState = rememberScrollState() // Permite el mover el contenido de la pantalla
    val scope = rememberCoroutineScope() // Ejecuta la espera de tiempo
    var isLoading by remember { mutableStateOf(false) } // Controla si se ve la carga
    var adminName by remember { mutableStateOf("")} // Variable para guardar el nombre de administrador
    var adminApPat by remember { mutableStateOf("")} // Varibale para guardar el apellido paterno del administrador
    var adminApMat by remember { mutableStateOf("")} // Variable para guardar el apellido materno del administrador
    var email by remember { mutableStateOf("")} // Variable para guardar el email
    var phoneNumber by remember { mutableStateOf("")} // Variable para guardar el numero de telefono
    var password by remember { mutableStateOf("")} // Varibale para guardar la contraseña
    var passwordConf by remember { mutableStateOf("")} // Varibale para guardar y confirmar la contraseña
    var hide by remember { mutableStateOf(true)}
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(// Apilamos para organizar los elementos de forma vertical
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Padding del Scafold
                    .padding(32.dp) // Margen para que no choque con los bordes
                    .verticalScroll(scrollState), // Permite el deslizar verticalmente
                horizontalAlignment = Alignment.CenterHorizontally, // Centra el contenido
                verticalArrangement = Arrangement.Center // Centra el contenido
            ) {
                Image(
                    painter = logogris,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(220.dp)
                        .height(220.dp)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp)) // Espacios para una mejor presentacion
                Text(
                    text = "Crear cuenta",
                    fontSize = 32.sp,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                TextField(
                    value = adminName,
                    onValueChange = { adminName = it }, // Actualiza la variable cuando el usuario escribe
                    label = { Text("Nombre(s)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                TextField(
                    value = adminApPat,
                    onValueChange = { adminApPat = it }, // Actualiza la variable cuando el usuario escribe
                    label = { Text("Apellido Paterno") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                TextField(
                    value = adminApMat,
                    onValueChange = { adminApMat = it }, // Actualiza la variable cuando el usuario escribe
                    label = { Text("Apellido Materno") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
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
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                TextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it }, // Actualiza la variable cuando el usuario escribe
                    label = { Text("Numero telefonico") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
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
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                TextField(
                    value = passwordConf,
                    onValueChange = { passwordConf = it },
                    label = { Text("Confirmar Contraseña") },
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
                Spacer(modifier = Modifier.height(16.dp)) // Espacios para una mejor presentacion
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ){
                    Button( // Botón para Registrar
                        onClick = {
                            scope.launch {
                                isLoading = true // Activa la carga
                                delay(2000) // Espera 2 segundos
                                isLoading = false // Desactica la carga
                                testNotification.value = true //Activa la notificacion
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = !isLoading
                    ) {
                        Text("Crear Cuenta")
                    }
                    Button( // Botón para Cancelar
                        onClick = onBackClick,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Cancelar")
                    }
                }
                TextButton(
                    onClick = {
                        notificationProblem.value = true
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Tiene algun problema para registrarse?")
                }
                if(testNotification.value) {
                    AlertDialog(
                        onDismissRequest = {
                            onBackClick()
                        },
                        title = {
                            Text(text = "Prueba")
                        },
                        text = {
                            Text(text = "Prueba de navegacion exitosa")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    onBackClick()
                                }
                            ) {
                                Text("Inicio de sesion")
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
                            Text(text = "Problema")
                        },
                        text = {
                            Text(text = "Favor de comunicarse a la empresa EventSync")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    notificationProblem.value = false
                                }
                            ) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
            }
        }
        LoadingOverlay(isLoading = isLoading) // Coloca el Overlay por encima de lo demas
    }
}