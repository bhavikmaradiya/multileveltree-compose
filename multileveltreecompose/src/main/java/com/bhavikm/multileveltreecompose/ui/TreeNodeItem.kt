package com.bhavikm.multileveltreecompose.ui

import android.content.ClipData
import android.content.ClipDescription
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bhavikm.multileveltreecompose.model.Node
import com.bhavikm.multileveltreecompose.utils.TreeManager.findClosestChild
import com.bhavikm.multileveltreecompose.utils.TreeManager.findNode
import com.bhavikm.multileveltreecompose.utils.TreeManager.isDescendantOf
import com.bhavikm.multileveltreecompose.utils.TreeManager.moveNode
import com.bhavikm.multileveltreecompose.utils.TreeManager.moveNodeToRoot

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> TreeNodeItem(
    node: Node<T>,
    lazyListState: LazyListState,
    treeState: MutableState<List<Node<T>>>,
    onExpand: (Int) -> Unit,
    expandedNodes: Set<Int>,
    item: @Composable (Node<T>) -> Unit,  // ✅ Custom UI from the user
) {
    val textMeasurer = rememberTextMeasurer()
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        val isValid =
                            event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        println("Should start drag: $isValid")
                        isValid
                    },
                    target =
                        remember {
                            object : DragAndDropTarget {
                                override fun onDrop(event: DragAndDropEvent): Boolean {
                                    println("Drag data triggered $event")
                                    val droppedId =
                                        event
                                            .toAndroidDragEvent()
                                            .clipData
                                            .getItemAt(0)
                                            ?.text
                                            ?.toString()
                                            ?.toIntOrNull()
                                    println("Drag data triggered $droppedId")
                                    if (droppedId == null) return false

                                    val fromNode =
                                        treeState.value.findNode(droppedId) ?: return false
                                    val targetNode =
                                        treeState.value.findNode(node.id)

                                    // If dropped outside the tree → move to root
                                    if (targetNode == null) {
                                        println("Dropped outside tree, moving to root")
                                        treeState.moveNodeToRoot(droppedId)
                                        return true
                                    }

                                    val dropPosition =
                                        Offset(
                                            event.toAndroidDragEvent().x,
                                            event.toAndroidDragEvent().y,
                                        )
                                    val closestChild =
                                        targetNode.findClosestChild(dropPosition, lazyListState)

                                    if (closestChild != null) {
                                        // Prevent dragging into itself or circular dependencies
                                        if (fromNode.id == targetNode.id /*||
                                            fromNode.isDescendantOf(
                                                closestChild,
                                                treeState.value,
                                            )*/
                                        ) {
                                            return false
                                        }
                                        println("Dropped inside child node ${closestChild.id}")
                                        treeState.moveNode(droppedId, closestChild.id)
                                    } else {
                                        if (targetNode.id != droppedId) {
                                            println("Dropped onto node ${targetNode.id} (as sibling)")
                                            treeState.moveNode(droppedId, targetNode.id)
                                        } else {
                                            return false
                                        }
                                    }

                                    return true
                                }
                            }
                        },
                ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .dragAndDropSource(
                            drawDragDecoration = {
                                drawRect(
                                    color = Color.LightGray,
                                    topLeft = Offset.Zero,
                                    size = Size(size.width, size.height),
                                )
                                val textLayoutResult =
                                    textMeasurer.measure(
                                        text = AnnotatedString(node.id.toString()),
                                        layoutDirection = layoutDirection,
                                        density = this,
                                        style =
                                            TextStyle(
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 16.sp,
                                                lineHeight = 24.0.sp,
                                                letterSpacing = 0.5.sp,
                                            ),
                                    )
                                drawText(
                                    textLayoutResult = textLayoutResult,
                                    topLeft =
                                        Offset(
                                            x = 10f,
                                            y = (size.height / 2) - (textLayoutResult.size.height / 2),
                                        ),
                                )
                            },
                        ) {
                            detectTapGestures(
                                onLongPress = { _ ->
                                    println("Drag started for node ${node.id}")
                                    startTransfer(
                                        DragAndDropTransferData(
                                            ClipData.newPlainText(
                                                "id",
                                                node.id.toString(),
                                            ),
                                            flags = View.DRAG_FLAG_GLOBAL,
                                        ),
                                    )
                                },
                            )
                        },
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) {
                    item(node)
                }
                if (node.children.isNotEmpty()) {
                    Text(
                        text = if (node.id in expandedNodes) "➖" else "➕", // Expand/collapse indicator
                        style = MaterialTheme.typography.bodyLarge,
                        modifier =
                            Modifier
                                .padding(start = 8.dp)
                                .clickable {
                                    onExpand.invoke(node.id)
                                },
                    )
                }
            }

            AnimatedVisibility(visible = node.id in expandedNodes) {
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    node.children.forEach { child ->
                        TreeNodeItem(
                            node = child,
                            lazyListState = lazyListState,
                            treeState = treeState,
                            onExpand = onExpand,
                            expandedNodes = expandedNodes,
                            item = item
                        )
                    }
                }
            }
        }
    }
}
