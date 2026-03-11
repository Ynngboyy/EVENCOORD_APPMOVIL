package com.example.eventcoord.ui.viewmodels


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.eventcoord.data.model.Evento
import com.example.eventcoord.data.model.Actividad
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
                listaEventos.clear()
                for (document in result) {
                    val evento = document.toObject(Evento::class.java)
                    listaEventos.add(evento)
                }
            }
            .addOnFailureListener { e ->
                // Manejar error de lectura
            }
    }
}