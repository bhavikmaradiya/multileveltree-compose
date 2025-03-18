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
                    Home(
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
fun Home(modifier: Modifier = Modifier) {
    val nodes = listOf(
        Node(1, data = "Root"),
        Node(2, parentId = 1, data = "Child 1"),
        Node(3, parentId = 1, data = "Child 2"),
        Node(4, parentId = 2, data = "Child 3"),
        Node(5, parentId = 2, data = "Child 4"),
        Node(6, parentId = 2, data = "Child 5")
    )

    TreeView(nodes, modifier) { node ->
        Text(text = node.data, modifier = Modifier.padding(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    TreeExampleTheme {
        Home()
    }
}
