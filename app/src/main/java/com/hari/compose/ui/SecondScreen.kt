package com.hari.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import com.hari.compose.ui.theme.ComposeTheme

class SecondScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BuildBody(
                        listOf(
                            "Mouse",
                            "Keyboard",
                            "Monitor",
                            "CPU",
                            "Controller",
                            "Speaker"
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun BuildBody(accessories: List<String>, modifier: Modifier = Modifier) {
    val constraintSet = ConstraintSet {
        val greenBox = createRefFor("greenBox")
        val accessoriesList = createRefFor("accessoriesList")

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
    }
    ConstraintLayout(constraintSet, modifier.fillMaxSize()) {
        Box(modifier = Modifier.layoutId("greenBox").background(color = Color.Cyan).height(100.dp).width(100.dp))
        LazyColumn(modifier = Modifier.layoutId("accessoriesList").padding(start = 16.dp)) {
            itemsIndexed(accessories) { index, item ->
                Text(
                    text = "$item is at index $index",
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitialPreview() {
    ComposeTheme {
        BuildBody(
            listOf("Mouse", "Keyboard", "Monitor", "CPU", "Controller", "Speaker"),
            modifier = Modifier.padding(16.dp)
        )
    }
}