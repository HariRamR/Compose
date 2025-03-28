package com.hari.compose.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hari.compose.ui.theme.ComposeTheme
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BuildBox(modifier = Modifier)
                }
            }
        }
    }
}

@Composable
fun BuildBox(modifier: Modifier) {
    val color = remember {
        mutableStateOf(Color.Red)
    }
    val tapCount = remember {
        mutableIntStateOf(0)
    }

    /*val actionClicked = remember {
        mutableStateOf(false)
    }*/

    val random = Random(256)
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = modifier
    ) { _ ->
        Box(
            modifier = Modifier.fillMaxSize().background(color = color.value),
            contentAlignment = Alignment.Center
        ) {
            Button(
                modifier = Modifier
                    .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                    .defaultMinSize(minWidth = 200.dp, minHeight = 50.dp),
                onClick = {
                    tapCount.intValue++
                    color.value = Color(
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256)
                    )
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            "Button clicked for $tapCount time(s)",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short,
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = color.value,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.7f),
                    disabledContentColor = Color.Gray.copy(alpha = 0.5f)
                ),
            ) {
                Text("Android")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitialPreview() {
    ComposeTheme {
        BuildBox(Modifier.padding(10.dp))
    }
}