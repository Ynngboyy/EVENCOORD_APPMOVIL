package com.example.eventcoord.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventcoord.data.model.Actividad
import com.example.eventcoord.data.model.Evento
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class EventDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var eventoCargado by mutableStateOf<Evento?>(null)
        private set

    fun cargarEventoPorId(eventoId: String) {
        viewModelScope.launch {
            // 1. Descargamos los datos principales del evento
            db.collection("eventos").document(eventoId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val eventoBasico = document.toObject(Evento::class.java)

                        if (eventoBasico != null) {
                            // 2. Inmediatamente después, descargamos la subcolección "programa"
                            db.collection("eventos").document(eventoId)
                                .collection("programa")
                                .get()
                                .addOnSuccessListener { snapshotPrograma ->
                                    val listaActividades = mutableListOf<Actividad>()

                                    for (doc in snapshotPrograma.documents) {
                                        val actividad = doc.toObject(Actividad::class.java)
                                        if (actividad != null) {
                                            listaActividades.add(actividad)
                                        }
                                    }

                                    // 3. Empaquetamos el evento y su programa juntos, y se lo damos a la vista
                                    eventoCargado = eventoBasico.copy(
                                        programa = listaActividades
                                    )
                                }
                                .addOnFailureListener {
                                    // Si falla la descarga del programa, mostramos el evento sin actividades
                                    eventoCargado = eventoBasico
                                }
                        }
                    } else {
                        eventoCargado = null
                    }
                }
                .addOnFailureListener {
                    eventoCargado = null
                }
        }
    }
}