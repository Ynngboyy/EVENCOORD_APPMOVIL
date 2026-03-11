package com.example.eventcoord.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore


data class Administrador(
    val nombre: String = "",
    val apPat: String = "",
    val apMat: String = "",
    val correo: String = "",
    val telefono: Long = 0L
)

data class Evento(
    val id: String = "",
    val titulo: String = "",
    val anfitrion: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val hora: String = "",
    val lugar: String = "",
    val creadorId: String = "",
    val programa: List<Actividad> = emptyList()
)

data class Actividad(
    val hora: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val esImportante: Boolean = false
)

class EventDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    // Aquí se guardará el evento cuando se cargue
    var eventoCargado by mutableStateOf<Evento?>(null)
        private set

    fun cargarEventoPorId(eventoId: String) {
        db.collection("eventos").document(eventoId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Convertimos el documento de Firebase al objeto Evento
                    eventoCargado = document.toObject(Evento::class.java)
                }
            }
    }
}