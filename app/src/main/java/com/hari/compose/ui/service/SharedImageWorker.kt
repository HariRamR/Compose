package com.hari.compose.ui.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class SharedImageWorker(
    private val appContext: Context,
    private val params: WorkerParameters
): CoroutineWorker(
    appContext, params,
) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val uncompressedURI = params.inputData.getString(KEY_UNCOMPRESSED_URI)
            uncompressedURI?.let {
                val actualUri = it.toUri()
                val bytes = appContext.contentResolver.openInputStream(actualUri)?.use { inpStream ->
                    inpStream.readBytes()
                } ?: return@withContext Result.failure()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                var outputBytes: ByteArray
                val outputStream = ByteArrayOutputStream()
                outputStream.use { os ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, os)
                    outputBytes = os.toByteArray()
                }
                val opFile = File(appContext.cacheDir, "${params.id}.jpg")
                opFile.writeBytes(outputBytes)
                Result.success(
                    workDataOf(
                        KEY_RESULT_PATH to opFile.absolutePath
                    )
                )

            } ?: return@withContext Result.failure()
        }
    }

    companion object {
        const val KEY_UNCOMPRESSED_URI = "KEY_UNCOMPRESSED_URI"
        const val KEY_RESULT_PATH = "KEY_RESULT_PATH"
    }
}