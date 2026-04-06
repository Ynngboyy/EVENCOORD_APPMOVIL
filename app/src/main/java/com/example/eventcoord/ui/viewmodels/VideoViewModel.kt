
package com.example.eventcoord.ui.viewmodels

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import androidx.core.graphics.drawable.toBitmap
import com.example.eventcoord.data.workers.VideoGenerationWorker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    var isProcessing by mutableStateOf(false)
        private set

    var progress by mutableFloatStateOf(0f)
        private set

    // Observa el progreso del Worker desde que se inicializa el ViewModel
    init {
        observarProgresoDelVideo()
    }

    private fun observarProgresoDelVideo() {
        viewModelScope.launch {
            workManager.getWorkInfosByTagFlow("video_generation_job").collect { workInfoList ->
                val workInfo = workInfoList.firstOrNull() ?: return@collect

                when (workInfo.state) {
                    WorkInfo.State.RUNNING -> {
                        isProcessing = true
                        progress = workInfo.progress.getFloat("progreso", 0f)
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        resetState()
                        Log.d("VideoVM", "Video procesado y subido con éxito.")
                    }
                    WorkInfo.State.FAILED -> {
                        resetState()
                        Log.e("VideoVM", "El Worker falló.")
                    }
                    else -> { }
                }
            }
        }
    }

    fun processNextBatch(eventoId: String) {
        if (isProcessing) return

        viewModelScope.launch {
            try {
                isProcessing = true
                progress = 0.1f
                val db = FirebaseFirestore.getInstance()

                // Obtener fotos pendientes
                val query = db.collection("eventos")
                    .document(eventoId)
                    .collection("fotos_revision")
                    .whereEqualTo("estado", "aceptado")
                    .whereEqualTo("uso_video", "pendiente")
                    .limit(10)
                    .get()
                    .await()

                val documents = query.documents
                if (documents.size < 10) {
                    Log.d("VideoVM", "No hay suficientes fotos (mínimo 10)")
                    resetState()
                    return@launch
                }

                val urls = documents.mapNotNull { it.getString("urlImagen") }
                val docIds = documents.map { it.id }

                // Descargar imágenes localmente
                progress = 0.2f
                val context = getApplication<Application>()
                val imageFiles = withContext(Dispatchers.IO) {
                    downloadImagesWithCoil(context, urls)
                }

                if (imageFiles.isEmpty()) {
                    resetState()
                    return@launch
                }

                // Lanzar el Worker con el id del evento
                val paths = imageFiles.map { it.absolutePath }.toTypedArray()
                val inputData = workDataOf(
                    "IMAGE_PATHS" to paths,
                    "EVENTO_ID" to eventoId
                )

                val videoRequest = OneTimeWorkRequestBuilder<VideoGenerationWorker>()
                    .setInputData(inputData)
                    .addTag("video_generation_job")
                    .build()

                workManager.enqueue(videoRequest)

            } catch (e: Exception) {
                Log.e("VideoVM", "Error en flujo: ${e.message}")
                resetState()
            }
        }
    }

    private suspend fun downloadImagesWithCoil(context: Context, urls: List<String>): List<File> {
        val imageLoader = ImageLoader(context)
        val files = mutableListOf<File>()
        val tempDir = File(context.filesDir, "temp_video_frames").apply {
            if (!exists()) mkdirs()
        }

        urls.forEachIndexed { index, url ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()
            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val bitmap = result.drawable.toBitmap()
                val file = File(tempDir, "frame_$index.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                files.add(file)
                bitmap.recycle()
            }
        }
        return files
    }

    fun actualizarFotosExistentes(eventoId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            try {
                val snapshot = db.collection("eventos").document(eventoId)
                    .collection("fotos_revision").whereEqualTo("estado", "aceptado").get().await()

                val batch = db.batch()
                snapshot.documents.filter { !it.contains("uso_video") }.forEach {
                    batch.update(it.reference, "uso_video", "pendiente")
                }
                batch.commit().await()
            } catch (e: Exception) { Log.e("JDCAR_DB", "Error: ${e.message}") }
        }
    }

    private fun resetState() {
        isProcessing = false
        progress = 0f
    }
}
