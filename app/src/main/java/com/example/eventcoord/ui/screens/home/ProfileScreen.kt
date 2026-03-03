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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import com.example.eventcoord.data.model.Administrador
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

fun probarConexionFirestore() {
    val db = Firebase.firestore
    db.collection("administradores")
        .get()
        .addOnSuccessListener { resultado ->
            for (documento in resultado) {
                Log.d("FIREBASE_TEST", "Documento ID: ${documento.id} => Datos: ${documento.data}")
                val admin = documento.toObject(Administrador::class.java)
                Log.d("FIREBASE_TEST", "¡Hola desde la nube, ${admin.nombre}!")
            }
        }
        .addOnFailureListener { error ->
            Log.e("FIREBASE_TEST", "Error al conectar: ", error)
        }
}
@Composable
fun ProfileScreen(onLogOut: () -> Unit, onBackClick: () -> Unit){
    // LEER DATOS DE FIREBASE
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    // DATOS DEL USUARIO
    val usuario = painterResource(R.drawable.usuario)
    var fullName by remember { mutableStateOf("Cargando...")} // Nombre completo
    var email by remember { mutableStateOf("Cargando...")} // Correo guardado
    var phone by remember { mutableStateOf("Cargando...")} // Telefono guardado
    //  VARIABLES DE ESTADO
    var isLoading by remember { mutableStateOf(false) } // Controla si se ve la carga
    var notificacionesActivadas by remember { mutableStateOf(true) }
    var modoOscuro by remember { mutableStateOf(false) }
    var actualSection by remember { mutableStateOf("Perfil") } // Controla que se muestra (Perfil/Configuracion)
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            db.collection("administradores").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()){
                        val nombre = document.getString("nombre") ?: "" // Obtenemos nombre
                        val apPat = document.getString("apPat") ?: "" // Obtenemos apellido paterno
                        val apMat = document.getString("apMat") ?: "" // Obtenemos apellido materno

                        fullName = "$nombre $apPat $apMat" // Juntamos el nombre comleto
                        email = document.getString("correo") ?: "" // Obtenemos el correo
                        phone = document.getString("telefono") ?: "" // Obtenemos el telefono
                    } else {
                        fullName = "Usuario no encontrado"
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    fullName = "Error al cargar los datos"
                    isLoading = false
                }
        } ?: run {
            isLoading = false
        }
    }
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Datos del perfil",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Nombre completo",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = fullName, // Tu variable de Firebase
                                    style = MaterialTheme.typography.bodyLarge, // Letra normal/grande
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                Text(
                                    text = "Correo electrónico",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = email, // Tu variable de Firebase
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                Text(
                                    text = "Teléfono",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray
                                )
                                Text(
                                    text = phone, // Tu variable de Firebase
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier= Modifier.width(8.dp))
                        TextButton(
                            onClick = {probarConexionFirestore()}
                        ) {
                            Text(
                                text = "Editar Perfil",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    "Configuracion" -> {
                        Text(
                            "General",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Notificaciones", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "Recibir alertas de clases y tareas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = notificacionesActivadas,
                                onCheckedChange = { notificacionesActivadas = it }
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Modo Oscuro", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "Cambiar apariencia de la app",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = modoOscuro,
                                onCheckedChange = { modoOscuro = it }
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        Text(
                            "Cuenta",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {}
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Cambiar Contraseña", style = MaterialTheme.typography.bodyLarge)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {}
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Ayuda y Soporte", style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        TextButton( // Botón para Cerrar Sesion
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                onLogOut()
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
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "EventCoord v0.6.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
        LoadingOverlay(isLoading = isLoading) // Coloca el Overlay por encima de lo demas
    }
}