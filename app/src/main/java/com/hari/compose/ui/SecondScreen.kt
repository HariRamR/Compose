package com.hari.compose

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.hari.compose.ui.service.SharedImageWorker
import com.hari.compose.ui.theme.ComposeTheme
import com.hari.compose.ui.vm.SharedImageVM

class SecondScreen : ComponentActivity() {

    private val viewModel by viewModels<SharedImageVM>()
    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(applicationContext)
        enableEdgeToEdge()
        setContent {
            ComposeTheme {
                val taskId = viewModel.workTaskId?.let {
                    workManager.getWorkInfoByIdLiveData(it).observeAsState().value
                }
                LaunchedEffect(key1 = taskId?.outputData) {
                    taskId?.outputData?.let { opData ->
                        val actualFilePath = opData.getString(SharedImageWorker.KEY_RESULT_PATH)
                        actualFilePath?.let {
                            val bitmap = BitmapFactory.decodeFile(actualFilePath)
                            viewModel.updateCompressedBitmap(bitmap)
                        }
                    }
                }
                if(taskId == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    SharedImageScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handleWorkRequest()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleWorkRequest()
    }

    private fun handleWorkRequest() {
        val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        } ?: return
        viewModel.updateUncompressedURI(imageUri)

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<SharedImageWorker>().setInputData(
            workDataOf(
                SharedImageWorker.KEY_UNCOMPRESSED_URI to imageUri.toString()
            )
        ).build()
        viewModel.updateWorkTaskId(oneTimeWorkRequest.id)
        workManager.enqueue(oneTimeWorkRequest)
    }

    @Composable
    fun SharedImageScreen() {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                viewModel.uncompressedURI?.let { uri ->
                    Text("Uncompressed image")
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                    )
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )
                }
                viewModel.compressedBitmap?.let { bitmap ->
                    Text("Compressed image")
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null
                    )
                }
            }
        }
    }
}