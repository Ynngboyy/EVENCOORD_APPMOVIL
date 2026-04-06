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

    // 1. Creamos dos listas
    val listaEventosActivos = mutableStateListOf<Evento>()
    val listaEventosPasados = mutableStateListOf<Evento>()

    fun cargarEventos() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("eventos")
            .whereEqualTo("creadorId", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error al cargar: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val hoy = LocalDate.now()
                    val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                    // Listas temporales para clasificar
                    val activosTemp = mutableListOf<Evento>()
                    val pasadosTemp = mutableListOf<Evento>()

                    snapshot.documents.forEach { doc ->
                        val evento = doc.toObject(Evento::class.java)?.copy(id = doc.id)
                        if (evento != null) {
                            try {
                                val fechaEvento = LocalDate.parse(evento.fecha, formato)

                                if (fechaEvento.isBefore(hoy)) {
                                    pasadosTemp.add(evento)
                                } else {
                                    activosTemp.add(evento)
                                }
                            } catch (e: DateTimeParseException) {
                                activosTemp.add(evento)
                            }
                        }
                    }

                    listaEventosActivos.clear()
                    listaEventosActivos.addAll(activosTemp)

                    listaEventosPasados.clear()
                    listaEventosPasados.addAll(pasadosTemp)
                }
            }
    }
}