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
import com.example.eventcoord.ui.components.LoadingOverlay
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.graphics.ColorFilter
import androidx.core.content.edit

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onForgotPassword: () -> Unit, onRegister: () -> Unit) {
    // VARIABLES DE ESTADO (IMPORTANTES PARA EL SISTEMA)
    var hide by remember { mutableStateOf(true) } // Mostrar/Ocultar contraseña
    val context = LocalContext.current // Obtenemos preferencias guardadas
    val sharedPreferences = remember { context.getSharedPreferences("EventCoordPrefs", Context.MODE_PRIVATE) } //Abrimos un guardado automatico
    var isRemember by remember { mutableStateOf(sharedPreferences.getBoolean("recordar_activo", false)) } // Toma o no en cuenta las preferencias
    var isLoading by remember { mutableStateOf(false) } // Controla si se ve la carga
    val logogris = painterResource(R.drawable.eventcoord_logo_v2) // Imagen del logo
    // NOTIFICACIONES
    val notificationProblem = remember { mutableStateOf(false)} // Problema con el inicio de sesión
    val notificationCampos = remember { mutableStateOf(false)} // Campos faltantes
    // VARIABLES DE USUARIO
    var email by remember { mutableStateOf(sharedPreferences.getString("correo_guardado", "") ?: "") } // Guardamos lo que el usuario escribe, y en caso de existir preferencias guardadas se usan
    var password by remember { mutableStateOf("") } // Guardamos lo que el usuario escribe
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
                        .width(320.dp)
                        .height(260.dp)
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
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
                        isLoading = true // Enpieza la carga
                        if ( email.isNotEmpty() && password.isNotEmpty()) {
                            val auth = FirebaseAuth.getInstance()
                            val db = FirebaseFirestore.getInstance()
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener { authResult ->
                                    val uid = authResult.user?.uid ?: ""
                                    db.collection("administradores").document(uid).get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                sharedPreferences.edit {
                                                    if (isRemember) { // Si marca la casilla se guarda el correo ingresado
                                                        putString("correo_guardado", email)
                                                        putBoolean("recordar_activo", true)
                                                    } else { // Si desmarco la casilla se borran los datos para no usarlos
                                                        remove("correo_guardado")
                                                        putBoolean("recordar_activo", false)
                                                    }
                                                }
                                                onLoginSuccess()
                                                isLoading = false
                                            } else {
                                                notificationProblem.value = true
                                                isLoading = false
                                            }
                                        }
                                        .addOnFailureListener {
                                            notificationProblem.value = true
                                            isLoading = false
                                        }
                                }
                                .addOnFailureListener { exception ->
                                    notificationProblem.value = true
                                    println("Error en Auth: ${exception.message}")
                                    isLoading = false
                                }
                        } else {
                            notificationCampos.value = true
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading, // Desactiva el boton si ya esta cargando
                            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC6A45C), // Tu color azul
                    contentColor = Color(0xFF7A7F86),         // Color del texto "Iniciar Sesión"
                    disabledContainerColor = Color(0xFFA8863B))
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
                if(notificationProblem.value) {
                    AlertDialog(
                        onDismissRequest = {
                            notificationProblem.value = false
                        },
                        title = {
                            Text(text = "Error")
                        },
                        text = {
                            Text(text = "Usuario o contraseña incorrectos")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    notificationProblem.value = false
                                }
                            ) {
                                Text("Ok")
                            }
                        }
                    )
                }
                if(notificationCampos.value) {
                    AlertDialog(
                        onDismissRequest = {
                            notificationCampos.value = false
                        },
                        title = {
                            Text(text = "Error al iniciar sesión")
                        },
                        text = {
                            Text(text = "Por favor asegurese de llenar todos los campos")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    notificationCampos.value = false
                                }
                            ) {
                                Text("Ok")
                            }
                        }
                    )
                }
            }
        }
        LoadingOverlay(isLoading = isLoading) // Coloca el Overlay por encima de lo demas
    }
}