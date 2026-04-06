package com.example.eventcoord.data.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume

class VideoGenerationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            val paths = inputData.getStringArray("IMAGE_PATHS") ?: return@withContext Result.failure()
            val eventoId = inputData.getString("EVENTO_ID") ?: return@withContext Result.failure()
            val imageFiles = paths.map { File(it) }

            // Generamos el video en 720p
            val videoFile = VideoEngine.runVideoGeneration(applicationContext, imageFiles) { progreso ->
                setProgressAsync(workDataOf("progreso" to (progreso * 0.7f)))
            }

            // Pausa estratégica para que el sistema operativo registre el tamaño del archivo
            delay(3000)
            System.gc()

            // Validación de integridad: Si pesa menos de 1KB, algo falló en el hardware
            if (videoFile != null && videoFile.exists() && videoFile.length() > 1024) {
                Log.d("JDCAR_WORKER", "Video válido generado: ${videoFile.length()} bytes")
                setProgressAsync(workDataOf("progreso" to 0.8f))

                val urlVideo = uploadToCloudinary(videoFile)

                if (urlVideo != null) {
                    saveToFirebase(urlVideo, eventoId)
                    marcarFotosComoUsadas(paths.toList(), eventoId)
                    setProgressAsync(workDataOf("progreso" to 1.0f))
                    Log.d("JDCAR_WORKER", "--- VIDEO COMPLETADO CON ÉXITO ---")
                    Result.success()
                } else {
                    Log.e("JDCAR_WORKER", "Error en la subida a Cloudinary")
                    Result.failure()
                }
            } else {
                Log.e("JDCAR_WORKER", "Archivo vacío (0 bytes). El hardware no escribió nada.")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("JDCAR_WORKER", "Fallo crítico: ${e.message}")
            Result.failure()
        }
    }

    private suspend fun marcarFotosComoUsadas(rutasLocales: List<String>, eventoId: String) {
        val db = FirebaseFirestore.getInstance()
        rutasLocales.chunked(10).forEach { chunk ->
            val batch = db.batch()
            try {
                val query = db.collection("fotos")
                    .whereEqualTo("eventoId", eventoId)
                    .whereIn("path_local", chunk)
                    .get().await()

                for (document in query.documents) {
                    batch.update(document.reference, "uso_video", true)
                }
                batch.commit().await()
            } catch (e: Exception) {
                Log.e("JDCAR_WORKER", "Error marcando fotos: ${e.message}")
            }
        }
    }

    private suspend fun uploadToCloudinary(file: File): String? = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(file.absolutePath)
            .option("resource_type", "video")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: MutableMap<Any?, Any?>?) {
                    val url = resultData?.get("secure_url")?.toString()
                    if (continuation.isActive) continuation.resume(url)
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    if (continuation.isActive) continuation.resume(null)
                }
                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }).dispatch()
    }

    private suspend fun saveToFirebase(videoUrl: String, eventoId: String) {
        val db = FirebaseFirestore.getInstance()
        val data = mapOf(
            "videoUrl" to videoUrl,
            "eventoId" to eventoId,
            "timestamp" to Timestamp.now(),
            "type" to "auto_batch"
        )
        db.collection("favoritos").add(data).await()
    }
}

object VideoEngine {

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun runVideoGeneration(context: Context, imageFiles: List<File>, onProgress: (Float) -> Unit): File? {
        if (imageFiles.isEmpty()) return null

        val outputVideo = File(context.cacheDir, "final_video_output.mp4")
        if (outputVideo.exists()) outputVideo.delete()

        val width = 1280
        val height = 720
        val frameRate = 15 // Reducimos a 15 FPS para dar más tiempo de proceso por frame
        val totalFramesPerImage = frameRate * 2 // 2 segundos por foto

        var codec: MediaCodec? = null
        var muxer: MediaMuxer? = null
        var eglHelper: com.example.eventcoord.ui.viewmodels.EglHelper? = null
        var inputSurface: android.view.Surface? = null
        var codecStarted = false

        try {
            val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
                setInteger(MediaFormat.KEY_BIT_RATE, 1_500_000)
                setInteger(MediaFormat.KEY_FRAME_RATE, 15)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

                // COMPATIBILIDAD UNIVERSAL: Baseline Profile (Evita errores de metadatos)
                setInteger(MediaFormat.KEY_COLOR_RANGE, MediaFormat.COLOR_RANGE_LIMITED)
                setInteger(MediaFormat.KEY_COLOR_STANDARD, MediaFormat.COLOR_STANDARD_BT709)
                setInteger(MediaFormat.KEY_COLOR_TRANSFER, MediaFormat.COLOR_TRANSFER_SDR_VIDEO)
            }
            codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            inputSurface = codec.createInputSurface()
            codec.start()
            codecStarted = true

            muxer = MediaMuxer(outputVideo.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            eglHelper = com.example.eventcoord.ui.viewmodels.EglHelper(inputSurface!!, width, height)
            eglHelper.makeCurrent()

            var trackIndex = -1
            var frameIndex = 0
            val bufferInfo = MediaCodec.BufferInfo()

            imageFiles.forEachIndexed { imgIdx, file ->
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(file.absolutePath, options)
                options.inSampleSize = calculateInSampleSize(options, width, height)
                options.inJustDecodeBounds = false
                options.inPreferredConfig = Bitmap.Config.RGB_565

                val originalBitmap = BitmapFactory.decodeFile(file.absolutePath, options) ?: return@forEachIndexed
                val frameBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(frameBitmap)
                canvas.drawColor(android.graphics.Color.BLACK)

                val scale = Math.min(width.toFloat() / originalBitmap.width, height.toFloat() / originalBitmap.height)
                val xOffset = (width - originalBitmap.width * scale) / 2f
                val yOffset = (height - originalBitmap.height * scale) / 2f

                val matrix = android.graphics.Matrix()
                matrix.postScale(scale, scale)
                matrix.postTranslate(xOffset, yOffset)
                canvas.drawBitmap(originalBitmap, matrix, android.graphics.Paint(android.graphics.Paint.FILTER_BITMAP_FLAG))

                repeat(totalFramesPerImage) {
                    // Limpiamos el buffer antes de dibujar para que no se mezcle "basura" anterior


                    eglHelper.drawBitmap(frameBitmap)

                    // Sincronización total
                    android.opengl.GLES20.glFlush()
                    android.opengl.GLES20.glFinish()

                    val presentationTimeNs = frameIndex * 1_000_000_000L / frameRate
                    eglHelper.setPresentationTime(presentationTimeNs)
                    eglHelper.swapBuffers()

                    // Pausa mínima solo para estabilidad
                    Thread.sleep(5)

                    var encoderIndex = codec?.dequeueOutputBuffer(bufferInfo, 30000) ?: -1
                    while (encoderIndex >= 0 || encoderIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        if (encoderIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                            val newFormat = codec?.outputFormat
                            if (newFormat != null) {
                                trackIndex = muxer?.addTrack(newFormat) ?: -1
                                muxer?.start()
                            }
                        } else if (encoderIndex >= 0) {
                            val encodedData = codec?.getOutputBuffer(encoderIndex)
                            if (encodedData != null && trackIndex != -1 && bufferInfo.size > 0) {
                                encodedData.position(bufferInfo.offset)
                                encodedData.limit(bufferInfo.offset + bufferInfo.size)
                                muxer?.writeSampleData(trackIndex, encodedData, bufferInfo)
                            }
                            codec?.releaseOutputBuffer(encoderIndex, false)
                        }
                        encoderIndex = codec?.dequeueOutputBuffer(bufferInfo, 0) ?: -1
                    }
                    frameIndex++
                }
                originalBitmap.recycle()
                frameBitmap.recycle()
                System.gc()
                onProgress((imgIdx + 1).toFloat() / imageFiles.size)
            }

            // FINALIZACIÓN CON TIEMPO EXTRA
            if (codecStarted) {
                codec?.signalEndOfInputStream()
                val finalInfo = MediaCodec.BufferInfo()
                var done = false
                while (!done) {
                    val index = codec?.dequeueOutputBuffer(finalInfo, 50000) ?: -1
                    if (index >= 0) {
                        val data = codec?.getOutputBuffer(index)
                        if (data != null && trackIndex != -1) muxer?.writeSampleData(trackIndex, data, finalInfo)
                        codec?.releaseOutputBuffer(index, false)
                        if ((finalInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) done = true
                    } else {
                        done = true
                    }
                }
                codec?.stop()
            }

            // PAUSA CRÍTICA ANTES DE CERRAR EL ARCHIVO
            // Esto asegura que el sistema de archivos de Android selle el MP4 correctamente
            Thread.sleep(1000)
            muxer?.stop()

            return if (outputVideo.exists() && outputVideo.length() > 0) outputVideo else null

        } catch (e: Exception) {
            Log.e("VideoEngine", "Error: ${e.message}")
            return null
        } finally {
            try {
                eglHelper?.release()
                inputSurface?.release()
                codec?.release()
                muxer?.release()
            } catch (e: Exception) { }
        }
    }
}
