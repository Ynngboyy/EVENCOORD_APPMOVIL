package com.example.eventcoord.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FotoItem(
    val id: String = "",
    val urlImagen: String = "",
    val estado: String = "",
    val fechaSubida: String = ""
)

class GaleriaViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _fotos = MutableStateFlow<List<FotoItem>>(emptyList())
    val fotos: StateFlow<List<FotoItem>> = _fotos

    fun cargarFotosRevision(eventoId: String) {
        db.collection("eventos")
            .document(eventoId)
            .collection("fotos_revision")
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.map { doc ->
                    val ts = doc.getTimestamp("fecha_subida")
                    val fechaTexto = ts?.toDate()?.toString() ?: "Fecha no disponible"

                    FotoItem(
                        id = doc.id,
                        urlImagen = doc.getString("urlImagen") ?: "",
                        estado = doc.getString("estado") ?: "pendiente",
                        fechaSubida = fechaTexto
                    )
                } ?: emptyList()
                _fotos.value = lista
            }
    }

    fun aceptarFoto(eventoId: String, foto: FotoItem) {
        val ref = db.collection("eventos").document(eventoId)
            .collection("fotos_revision").document(foto.id)

        ref.update("estado", "aceptado")
    }

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
            .whereEqualTo("estado", "aceptado")
            .addSnapshotListener { snapshot, _ ->
                val lista = snapshot?.map { doc ->
                    val ts = doc.getTimestamp("fecha_subida")
                    val fechaTexto = ts?.toDate()?.toString() ?: ""

                    FotoItem(
                        id = doc.id,
                        urlImagen = doc.getString("urlImagen") ?: "",
                        estado = "aceptado",
                        fechaSubida = fechaTexto
                    )
                } ?: emptyList()
                _favoritos.value = lista
            }
    }
}