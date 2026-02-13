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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eventcoord.R

@Composable
fun PasswordScreen(onBackClick: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val logogris = painterResource(R.drawable.eventcoord_logo_gris) // Imagen de Logo
        val testNotification = remember { mutableStateOf(false)} // Notificacion de correo enviado
        var email by remember { mutableStateOf("")} // Variable para guardar el email
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
                    .padding(8.dp)
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
                onValueChange = { email = it }, // Actualiza la variable cuando el usuario escribe
                label = { Text("Correo Electronico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                singleLine = true,
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
                    onClick = { testNotification.value = true },
                    modifier = Modifier.padding(8.dp)
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
        }
    }
}