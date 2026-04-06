package com.example.eventcoord.ui.screens.home

import android.content.Context
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.core.content.edit
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import com.example.eventcoord.ui.uriToBase64
import com.example.eventcoord.ui.base64ToImageBitmap
import com.google.firebase.auth.EmailAuthProvider

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
    var profileImageBase64 by remember { mutableStateOf("") } // texto de la foto
    var currentPass by remember { mutableStateOf("") } // Contraseña actual (para re-autenticar)
    var newPass by remember { mutableStateOf("") } // Contraseña nueva
    var confirmPass by remember { mutableStateOf("") } // Confirmar contraseña nueva// Confirmar contraseña nueva
    // VARIBLES TEMPORALES PARA EDITAR DATOS DE USUARIO
    var tempName by remember { mutableStateOf("") } // Varible temporal en caso de querer editar el nombre
    var tempApPat by remember { mutableStateOf("") }
    var tempApMat by remember { mutableStateOf("") }
    var tempPhone by remember { mutableStateOf("") }
    //  VARIABLES DE ESTADO
    var isLoading by remember { mutableStateOf(false) } // Controla si se ve la carga
    var actualSection by remember { mutableStateOf("Perfil") } // Controla que se muestra (Perfil/Configuracion)
    var expanded by remember { mutableStateOf(false) } // Menú de temas
    val themeOptions = listOf("Modo claro", "Modo oscuro", "Igual que el sistema") // Lista de temas
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE) }
    var selectedTheme by remember {
        mutableStateOf(sharedPrefs.getString("tema_app", "Igual que el sistema") ?: "Igual que el sistema")
    }
    var isEditing by remember { mutableStateOf(false) } // Variable para saber si esta editando información
    var mensajeResultado by remember { mutableStateOf("") } // Variable que guarda texto para conocer el resultado
    // Notificaciones
    val notificationErrEd = remember { mutableStateOf(false) } // Notificacion de error al actualizar
    var notificationPass by remember { mutableStateOf(false) } // Notificacion para cambiar la contraseña
    var notificationResult by remember { mutableStateOf(false) } // Notificacion para saber el resultado de cambiar la contraseña
    var notificationHelp by remember { mutableStateOf(false) }// Notificacion para obtener ayuda
    // LANZADOR PARA ABRIR LA GALERIA DEL TELEFONO
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                isLoading = true
                val base64String = uriToBase64(context, uri)
                if (base64String != null) {
                    val uid = auth.currentUser?.uid ?: return@rememberLauncherForActivityResult
                    db.collection("administradores").document(uid)
                        .update("fotoPerfil", base64String)
                        .addOnSuccessListener {
                            profileImageBase64 = base64String
                            isLoading = false
                        }
                        .addOnFailureListener {
                            isLoading = false
                            notificationErrEd.value = true
                        }
                } else {
                    isLoading = false
                }
            }
        }
    )
    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            db.collection("administradores").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()){
                        val nombre = document.getString("nombre") ?: "" // Obtenemos nombre
                        val apPat = document.getString("apPat") ?: "" // Obtenemos apellido paterno
                        val apMat = document.getString("apMat") ?: "" // Obtenemos apellido materno
                        val fotoGuardada = document.getString("fotoPerfil") ?: "" // Obtenemos el texto de la imagen
                        profileImageBase64 = fotoGuardada

                        fullName = "$nombre $apPat $apMat" // Juntamos el nombre comleto
                        email = document.getString("correo") ?: "" // Obtenemos el correo
                        phone = document.getString("telefono") ?: "" // Obtenemos el telefono

                        tempName = nombre
                        tempApPat = apPat
                        tempApMat = apMat
                        tempPhone = phone
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
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (actualSection == "Perfil") "Mi Perfil" else "Mi Configuración",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            bottomBar= {
                Card(modifier = Modifier.fillMaxWidth().navigationBarsPadding(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), shape = androidx.compose.ui.graphics.RectangleShape) {
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
                                textAlign = TextAlign.Center,color = if(actualSection == "Perfil") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier= Modifier.width(8.dp))
                        TextButton(
                            onClick = { actualSection = "Configuracion"}
                        ) {
                            Text(
                                text = "Configuración",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,color = if(actualSection == "Configuracion") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
                        Box(
                            modifier = Modifier.clickable {
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        ) {
                            if (profileImageBase64.isNotEmpty() && base64ToImageBitmap(profileImageBase64) != null) {
                                Image(
                                    bitmap = base64ToImageBitmap(profileImageBase64)!!,
                                    contentDescription = "Foto de usuario real",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                )
                            } else {
                                Image(
                                    painter = usuario,
                                    contentDescription = "Foto de usuario",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                if (isEditing) {
                                    TextField(
                                        value = tempName,
                                        onValueChange = {
                                            tempName = it
                                        }, // Actualiza la variable cuando el usuario escribe
                                        label = { Text("Nombre(s)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                        singleLine = true,
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            disabledContainerColor = Color.Transparent,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    TextField(
                                        value = tempApPat,
                                        onValueChange = {
                                            tempApPat = it
                                        }, // Actualiza la variable cuando el usuario escribe
                                        label = { Text("Apellido Paterno") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                        singleLine = true,
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            disabledContainerColor = Color.Transparent,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    TextField(
                                        value = tempApMat,
                                        onValueChange = {
                                            tempApMat = it
                                        }, // Actualiza la variable cuando el usuario escribe
                                        label = { Text("Apellido Materno") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                        singleLine = true,
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            disabledContainerColor = Color.Transparent,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                } else {
                                    Text(
                                        text = fullName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                                Text(
                                    text = "Correo electrónico",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = email,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                                Text(
                                    text = "Teléfono",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                if (isEditing) {
                                    TextField(
                                        value = tempPhone,
                                        onValueChange = {
                                            tempPhone = it
                                        }, // Actualiza la variable cuando el usuario escribe
                                        label = { Text("Numero Telefonico") },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Phone,
                                            imeAction = ImeAction.Done
                                        ),
                                        singleLine = true,
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            disabledContainerColor = Color.Transparent,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                } else {
                                    Text(
                                        text = phone,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            if (isEditing) {
                                TextButton(onClick = {
                                    val parts = fullName.split(" ")
                                    tempName = parts.getOrNull(0) ?: ""
                                    tempApPat = parts.getOrNull(1) ?: ""
                                    tempApMat = parts.getOrNull(2) ?: ""
                                    tempPhone = phone

                                    isEditing = false
                                }) {
                                    Text("Cancelar", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            TextButton(
                                onClick = {
                                    if (isEditing) {
                                        val uid = auth.currentUser?.uid ?: return@TextButton
                                        isLoading = true
                                        val updates = hashMapOf<String, Any>(
                                            "nombre" to tempName,
                                            "apPat" to tempApPat,
                                            "apMat" to tempApMat,
                                            "telefono" to tempPhone
                                        )
                                        db.collection("administradores").document(uid)
                                            .update(updates)
                                            .addOnSuccessListener {
                                                fullName = "$tempName $tempApPat $tempApMat"
                                                phone = tempPhone
                                                isLoading = false
                                                isEditing = false
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                notificationErrEd.value = true
                                                println("Error al actualizar perfil estudiante: ${e.message}")
                                            }
                                    } else {
                                        isEditing = true
                                    }
                                }
                            ) {
                                Text(
                                    text = if (isEditing) "Guardar Cambios" else "Editar perfil",
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
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
                                .clickable { expanded = true }
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Tema de la aplicación", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    selectedTheme,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                            Box{
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Seleccionar tema",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    themeOptions.forEach { option->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                selectedTheme = option
                                                expanded = false
                                                sharedPrefs.edit { putString("tema_app", option) }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                        Text(
                            "Cuenta",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { notificationPass = true}
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Cambiar Contraseña", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { notificationHelp = true}
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Ayuda y Soporte", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
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
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "EventCoord v0.6.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
        LoadingOverlay(isLoading = isLoading) // Coloca el Overlay por encima de lo demas
        if(notificationErrEd.value) {
            AlertDialog(
                onDismissRequest = {
                    notificationErrEd.value = false
                },
                title = {
                    Text(text = "Error")
                },
                text = {
                    Text(text = "Ocurrio un error al actualizar los datos del perfil")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            notificationErrEd.value = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                }
            )
        }
        if (notificationPass) {
            AlertDialog(
                onDismissRequest = { notificationPass = false },
                title = { Text("Cambiar Contraseña") },
                text = {
                    Column {
                        Text(
                            text = "Por seguridad, ingresa tu contraseña actual para confirmar el cambio.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = currentPass,
                            onValueChange = { currentPass = it },
                            label = { Text("Contraseña actual") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newPass,
                            onValueChange = { newPass = it },
                            label = { Text("Nueva contraseña") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmPass,
                            onValueChange = { confirmPass = it },
                            label = { Text("Confirmar nueva") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                                mensajeResultado = "Por favor, llena todos los campos."
                                notificationResult = true
                                return@Button
                            }
                            if (newPass == confirmPass) {
                                if (newPass.length >= 8) {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    val emailUser = user?.email

                                    if (user != null && emailUser != null) {
                                        isLoading = true
                                        val credential = EmailAuthProvider.getCredential(emailUser, currentPass)
                                        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                                            if (reauthTask.isSuccessful) {
                                                user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                                                    isLoading = false
                                                    if (updateTask.isSuccessful) {
                                                        mensajeResultado = "Contraseña actualizada correctamente"
                                                        notificationPass = false
                                                        notificationResult = true

                                                        currentPass = ""
                                                        newPass = ""
                                                        confirmPass = ""
                                                    } else {
                                                        mensajeResultado = "Error al actualizar: ${updateTask.exception?.message}"
                                                        notificationResult = true
                                                    }
                                                }
                                            } else {
                                                isLoading = false
                                                mensajeResultado = "La contraseña actual es incorrecta."
                                                notificationResult = true
                                            }
                                        }
                                    }
                                } else {
                                    mensajeResultado = "La contraseña debe tener mínimo 8 caracteres."
                                    notificationResult = true
                                }
                            } else {
                                mensajeResultado = "Las contraseñas nuevas no coinciden."
                                notificationResult = true
                            }
                        }
                    ) { Text("Actualizar") }
                },
                dismissButton = {
                    TextButton(onClick = { notificationPass = false }) { Text("Cancelar") }
                }
            )
        }
        if (notificationResult) {
            AlertDialog(
                onDismissRequest = { notificationResult = false },
                title = { Text("Aviso") },
                text = { Text(mensajeResultado) },
                confirmButton = {
                    TextButton(onClick = { notificationResult = false }) { Text("OK") }
                }
            )
        }
        if (notificationHelp) {
            AlertDialog(
                onDismissRequest = { notificationHelp = false },
                icon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null) },
                title = { Text("Centro de Ayuda") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text("Preguntas Frecuentes", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        PreguntaRespuesta(
                            "¿Cómo acepto las fotos que recibo?",
                            "En las seccion de *Galería* solo debes de deslizar la foto a la derecha para aceptarla o a la izquierda para denegarla"
                        )
                        PreguntaRespuesta(
                            "¿Cómo elimino un evento?",
                            "Entra a el evento, toca el ícono de engranaje ⚙️ arriba a la derecha y selecciona la opción de eliminar."
                        )
                        PreguntaRespuesta(
                            "¿Comó obtengo el video recopilatorio de las fotos?",
                            "Entra a la sección de *Fotos Guardadas* y arriba a la derecha e el icono de descarga los encontraras"
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        Text("Soporte Técnico", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                        Text("Si tienes problemas con tu cuenta, contáctanos:", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Suport@eventsync.org.mx", fontWeight = FontWeight.Bold)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { notificationHelp = false }) {
                        Text("Entendido")
                    }
                }
            )
        }
    }
}

@Composable
fun PreguntaRespuesta(pregunta: String, respuesta: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(pregunta, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(respuesta, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}