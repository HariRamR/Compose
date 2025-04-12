package com.hari.compose.ui.vm

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID

class SharedImageVM: ViewModel() {
    var uncompressedURI: Uri? by mutableStateOf(null)
        private set
    var workTaskId: UUID? by mutableStateOf(null)
        private set
    var compressedBitmap: Bitmap? by mutableStateOf(null)
        private set

    fun updateUncompressedURI(uri: Uri) {
        this.uncompressedURI = uri
    }

    fun updateWorkTaskId(uuid: UUID) {
        this.workTaskId = uuid
    }

    fun updateCompressedBitmap(bitmap: Bitmap) {
        this.compressedBitmap = bitmap
    }
}