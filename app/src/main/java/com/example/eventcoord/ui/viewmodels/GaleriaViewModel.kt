package com.example.eventcoord.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.model.Values.timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class FotoItem(
    val id: String = "",
    val url: String = "",
    val estado: String = "pendiente",
    val fecha_subida: String = ""
)

class GaleriaViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _fotos = MutableStateFlow<List<FotoItem>>(emptyList())
    val fotos: StateFlow<List<FotoItem>> = _fotos

    // Función para cargar fotos de un evento específico
    fun cargarFotosRevision(eventoId: String) {
        db.collection("eventos")
            .document(eventoId)
            .collection("fotos_revision")
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.map { doc ->
                    val ts = doc.getTimestamp("fecha_subida")
                    // se converte en String
                    val fechaTexto = ts?.toDate()?.toString() ?: "Fecha no disponible"

                    // objeto FotoItem
                    FotoItem(
                        id = doc.id,
                        url = doc.getString("url") ?: "",
                        estado = doc.getString("estado") ?: "pendiente",
                        fecha_subida = fechaTexto // Pasamos el valor ya procesado
                    )
                } ?: emptyList()
                _fotos.value = lista
            }
    }

    // Al aceptar (Swipe Derecha): Cambiamos estado a "aceptado" y movemos a favoritos
    fun aceptarFoto(eventoId: String, foto: FotoItem) {
        val ref = db.collection("eventos").document(eventoId)
            .collection("fotos_revision").document(foto.id)

        ref.update("estado", "aceptado")
        db.collection("favoritos").document(foto.id).set(foto)
    }

    // Al descartar (Swipe Izquierda): Cambiamos estado a "rechazado" o eliminamos
    fun rechazarFoto(eventoId: String, foto: FotoItem) {
        db.collection("eventos").document(eventoId)
            .collection("fotos_revision").document(foto.id)
            .update("estado", "rechazado")
    }

    private val _favoritos = MutableStateFlow<List<FotoItem>>(emptyList())
    val favoritos: StateFlow<List<FotoItem>> = _favoritos

    fun cargarFavoritos(eventoId: String) {
        db.collection("eventos")
            .document(eventoId)
            .collection("fotos_revision")
            .whereEqualTo("estado", "aceptado") // Solo las que diste swipe a la derecha
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.map { doc ->
                    FotoItem(
                        id = doc.id,
                        url = doc.getString("url") ?: "",
                        estado = "aceptado"
                    )
                } ?: emptyList()
                _favoritos.value = lista
            }
    }
}