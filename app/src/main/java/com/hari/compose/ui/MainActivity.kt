package com.hari.compose.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hari.compose.ui.theme.ComposeTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = BuildBoxRoute
                ) {
                    composable<BuildBoxRoute> {
                        BuildBox(
                            {
                                navController.navigate(
                                    UiComponentsRoute(
                                        accessories = listOf(
                                            "Mouse",
                                            "Keyboard",
                                            "Monitor",
                                            "CPU",
                                            "Controller",
                                            "Speaker"
                                        ),
                                    ),
                                )
                            }
                        )
                    }
                    composable<UiComponentsRoute> {
                        val args = it.toRoute<UiComponentsRoute>()
                        UiComponentsScreen(args.accessories)
                    }
                }
            }
        }
    }
}

@Serializable
object BuildBoxRoute

@Composable
fun BuildBox(
    onClick: () -> Unit
) {
    /*val color = remember {
        mutableStateOf(Color.Red)
    }*/
    val tapCount = remember {
        mutableIntStateOf(0)
    }

    /*val actionClicked = remember {
        mutableStateOf(false)
    }*/

//    val random = Random(256)
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val rememberInfiniteTransition = rememberInfiniteTransition()
    val color = rememberInfiniteTransition.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Green,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
            ),
            repeatMode = RepeatMode.Reverse,
        )
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { _ ->

        Box(
            modifier = Modifier.fillMaxSize().background(color = color.value),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Button(
                    modifier = Modifier
                        .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                        .defaultMinSize(minWidth = 200.dp, minHeight = 50.dp),
                    onClick = {
                        tapCount.intValue++
                        /*color.value = Color(
                            random.nextInt(256),
                            random.nextInt(256),
                            random.nextInt(256),
                            random.nextInt(256)
                        )*/
                        coroutineScope.launch {
                            val snackBarResult = snackBarHostState.showSnackbar(
                                "Button clicked for $tapCount time(s)",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short,
                            )
                            when (snackBarResult) {
                                SnackbarResult.Dismissed -> {
                                    print("Snackbar Dismissed")
                                }

                                SnackbarResult.ActionPerformed -> {
                                    print("Snackbar action performed")
                                }
                            }
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
                Button(
                    modifier = Modifier
                        .border(2.dp, Color.White, RoundedCornerShape(10.dp))
                        .defaultMinSize(minWidth = 200.dp, minHeight = 50.dp),
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp),
                ) {
                    Text(
                        "UI Components"
                    )
                }
            }
        }
    }
}

@Serializable
data class UiComponentsRoute(
    val accessories: List<String>
)

@Composable
fun UiComponentsScreen(accessories: List<String>) {
    val constraintSet = ConstraintSet {
        val greenBox = createRefFor("greenBox")
        val accessoriesList = createRefFor("accessoriesList")
        val circularProgressBar = createRefFor("circularProgressBar")

        constrain(greenBox) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            width = Dimension.value(100.dp)
            height = Dimension.value(100.dp)
        }

        constrain(accessoriesList) {
            top.linkTo(parent.top)
            start.linkTo(greenBox.end)
        }

        constrain(circularProgressBar) {
            top.linkTo(accessoriesList.bottom)
            centerHorizontallyTo(parent)
        }
    }

    var isAnimationEnded by remember {
        mutableStateOf(false)
    }
    val progressState = animateFloatAsState(
        targetValue = if (isAnimationEnded) 0.8f else 0f,
        animationSpec = tween(
            durationMillis = 3000,
            delayMillis = 1000,
        )
    )

    LaunchedEffect(true) {
        isAnimationEnded = true
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        ConstraintLayout(constraintSet, Modifier.fillMaxSize().padding(innerPadding)) {
            Box(
                modifier = Modifier.layoutId("greenBox").background(color = Color.Cyan)
                    .height(100.dp)
                    .width(100.dp)
            )
            LazyColumn(modifier = Modifier.layoutId("accessoriesList").padding(start = 16.dp)) {
                itemsIndexed(accessories) { index, item ->
                    Text(
                        text = "$item is at index $index",
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
            Box(
                modifier = Modifier.layoutId("circularProgressBar").size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.Green,
                        startAngle = -90f,
                        sweepAngle = 360 * progressState.value,
                        useCenter = false,
                        style = Stroke(width = 30f, cap = StrokeCap.Round)
                    )
                }
                Text(
                    (progressState.value * 100).toInt().toString(),
                    color = Color.Black,
                )
            }
        }
    }
}