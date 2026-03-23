package com.example.eventcoord.ui.viewmodels


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.eventcoord.data.model.Evento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class EventosViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    val listaEventos = mutableStateListOf<Evento>()
    fun cargarEventos() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("eventos")
            .whereEqualTo("creadorId", uid)
            .get()
            .addOnSuccessListener { result ->
                val hoy = LocalDate.now()
                val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val eventosActivos = result.documents.mapNotNull { doc ->
                    val evento = doc.toObject(Evento::class.java)?.copy(id = doc.id)
                    if (evento != null) {
                        try {
                            val fechaEvento = LocalDate.parse(evento.fecha, formato)
                            if (fechaEvento.isBefore(hoy)) null else evento
                        } catch (e: DateTimeParseException){
                            evento
                        }
                    } else {
                        null
                    }
                }
                listaEventos.clear()
                listaEventos.addAll(eventosActivos)
            }
            .addOnFailureListener { e ->
                println("Error al cargar: ${e.message}")
            }
    }
}