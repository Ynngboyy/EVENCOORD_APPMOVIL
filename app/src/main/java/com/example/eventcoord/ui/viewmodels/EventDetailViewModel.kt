package com.example.eventcoord.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventcoord.data.model.Actividad
import com.example.eventcoord.data.model.Evento
import com.example.eventcoord.data.model.Invitado
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class EventDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var eventoCargado by mutableStateOf<Evento?>(null)
        private set
    var listaInvitados by mutableStateOf<List<Invitado>>(emptyList())
        private set
    fun escucharInvitados(eventoId: String) {
        db.collection("eventos").document(eventoId).collection("invitados")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error cargando invitados: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    listaInvitados = snapshot.documents.mapNotNull {
                        it.toObject(Invitado::class.java)
                    }
                }
            }
    }
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
                                        val actividad = doc.toObject(Actividad::class.java)?.copy(id = doc.id)
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
    fun eliminarEventoTotal(eventoId: String, onSuccess: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        // Borramos el documento principal del evento
        db.collection("eventos").document(eventoId).delete()
            .addOnSuccessListener {
                onSuccess() // Le avisamos a la pantalla que ya terminó para que se cierre
            }
            .addOnFailureListener { e ->
                println("Error al eliminar evento: ${e.message}")
            }
    }
    fun agregarActividad(eventoId: String, actividad: Actividad) {
        val db = FirebaseFirestore.getInstance()
        val nuevaRef = db.collection("eventos").document(eventoId).collection("programa").document()

        val actividadConId = actividad.copy(id = nuevaRef.id)

        nuevaRef.set(actividadConId).addOnSuccessListener {
            // Actualizamos la pantalla al instante
            val eventoActual = eventoCargado
            if (eventoActual != null) {
                val nuevaLista = eventoActual.programa.toMutableList()
                nuevaLista.add(actividadConId)
                eventoCargado = eventoActual.copy(programa = nuevaLista)
            }
        }
    }

    fun eliminarActividad(eventoId: String, actividadId: String) {
        val db = FirebaseFirestore.getInstance()

        db.collection("eventos").document(eventoId).collection("programa").document(actividadId).delete()
            .addOnSuccessListener {
                // Removemos la actividad de la pantalla al instante
                val eventoActual = eventoCargado
                if (eventoActual != null) {
                    val nuevaLista = eventoActual.programa.filter { it.id != actividadId }
                    eventoCargado = eventoActual.copy(programa = nuevaLista)
                }
            }
    }
}