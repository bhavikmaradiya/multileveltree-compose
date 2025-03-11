package com.example.treeexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bhavikm.multileveltreecompose.model.Node
import com.bhavikm.multileveltreecompose.ui.TreeView
import com.example.treeexample.ui.theme.TreeExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TreeExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Greeting(
                        name = "Android",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val nodes = listOf(
        Node(1, data = "Root"),
        Node(2, parentId = 1, data = "Child 1"),
        Node(3, parentId = 1, data = "Child 2")
    )

    TreeView(nodes, modifier) { node ->
        Text(text = node.data, modifier = Modifier.padding(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TreeExampleTheme {
        Greeting("Android")
    }
}