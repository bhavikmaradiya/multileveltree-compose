package com.bhavikm.multileveltreecompose.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import com.bhavikm.multileveltreecompose.model.Node
import com.bhavikm.multileveltreecompose.utils.TreeManager.createTreeFrom
import com.bhavikm.multileveltreecompose.utils.TreeManager.moveNodeToRoot

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> TreeView(
    nodes: List<Node<T>>,
    modifier: Modifier = Modifier,
    itemCompose: @Composable (Node<T>) -> Unit,
) {
    val treeState = remember {
        mutableStateOf(nodes.createTreeFrom(originalNodes = nodes))
    }
    val lazyListState = rememberLazyListState()
    var expandedNodes by remember { mutableStateOf(setOf<Int>()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains("text/plain")
                },
                target = remember {
                    object : DragAndDropTarget {
                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            val droppedId =
                                event.toAndroidDragEvent()
                                    .clipData
                                    .getItemAt(0)
                                    ?.text
                                    ?.toString()
                                    ?.toIntOrNull()
                            if (droppedId != null) {
                                treeState.moveNodeToRoot(droppedId)
                                return true
                            }
                            return false
                        }
                    }
                }
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState
        ) {
            items(treeState.value, key = { it.id }) { node ->
                TreeNodeItem(
                    node = node,
                    lazyListState = lazyListState,
                    treeState = treeState,
                    onExpand = { nodeId ->
                        expandedNodes = if (expandedNodes.contains(nodeId)) {
                            expandedNodes - nodeId
                        } else {
                            expandedNodes + nodeId
                        }
                    },
                    expandedNodes = expandedNodes,
                    item = itemCompose
                )
            }
        }
    }
}
