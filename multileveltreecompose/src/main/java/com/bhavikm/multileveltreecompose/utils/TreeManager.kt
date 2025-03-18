package com.bhavikm.multileveltreecompose.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import com.bhavikm.multileveltreecompose.model.Node


object TreeManager {

    /**
     * Creates a tree structure from a flat list of nodes.
     *
     * @param originalNodes The flat list of nodes to build the tree from.
     * @return A list of root nodes representing the tree structure.
     */
    fun <T> List<Node<T>>.createTreeFrom(originalNodes: List<Node<T>>): List<Node<T>> =
        filter {
            if (originalNodes.size == size) {
                it.parentId == null
            } else {
                true
            }
        }.map { node ->
            node.copy(
                children =
                    originalNodes
                        .filter {
                            it.parentId == node.id
                        }.toMutableList()
                        .createTreeFrom(originalNodes)
                        .toMutableList(),
            )
        }

    /**
     * Finds and removes a node from the tree.
     * Returns a new tree list (without the node) and the removed node.
     */
    fun <T> List<Node<T>>.findAndRemoveNode(nodeId: Int): Pair<List<Node<T>>, Node<T>?> {
        val mutableList = this.toMutableList()
        val iterator = mutableList.iterator()

        while (iterator.hasNext()) {
            val node = iterator.next()
            if (node.id == nodeId) {
                iterator.remove()
                return Pair(mutableList, node)
            }
            val (newChildren, foundNode) = node.children.findAndRemoveNode(nodeId)
            if (foundNode != null) {
                node.children = newChildren.toMutableList()
                return Pair(mutableList, foundNode)
            }
        }
        return Pair(this, null)
    }

    /**
     * Moves a node to the root level of the tree.
     *
     * @param nodeId The ID of the node to move to the root.
     * @param T The type of data stored in the nodes.
     */
    /**
     * Moves a node to the root level.
     * Returns a new tree list with the node at the root.
     */
    fun <T> MutableState<List<Node<T>>>.moveNodeToRoot(nodeId: Int) {
        val currentTree = value.toMutableList()
        val nodeToMove = currentTree.findNode(nodeId) ?: return
        nodeToMove.parentId = null
        value = currentTree.removeNode(nodeId).apply { add(nodeToMove) }.toList()
    }


    /**
     * Moves a node to be a child of a new parent node.
     *
     * @param nodeId The ID of the node to move.
     * @param newParentId The ID of the new parent node.
     */
    fun <T> MutableState<List<Node<T>>>.moveNode(
        nodeId: Int,
        newParentId: Int,
    ) {
        val nodes = value

        val nodeToMove = nodes.findNode(nodeId) ?: return
        val newParent = nodes.findNode(newParentId) ?: return

        if (nodeToMove.isDirectDescendantOf(newParent, nodes)) return

        val updatedTree = nodes.removeNode(nodeId).toList()

        val finalTree =
            updatedTree
                .addNode(
                    nodeToMove.copy(parentId = newParentId),
                    newParentId,
                ).toList()

        value = finalTree
    }

    /**
     * Removes a node from the tree by its ID.
     *
     * @param nodeId The ID of the node to remove.
     * @return A new list of nodes with the specified node removed.
     */
    private fun <T> List<Node<T>>.removeNode(nodeId: Int): MutableList<Node<T>> {
        return mapNotNull { node ->
            if (node.id == nodeId) return@mapNotNull null
            node.copy(
                children =
                    node.children.removeNode(
                        nodeId,
                    ),
            )
        }.toMutableList()
    }

    /**
     * Adds a node to the tree as a child of the specified target node.
     *
     * @param nodeToAdd The node to add to the tree.
     * @param targetId The ID of the node to add the new node under.
     * @return A new list of nodes with the new node added.
     */
    private fun <T> List<Node<T>>.addNode(
        nodeToAdd: Node<T>,
        targetId: Int,
    ): MutableList<Node<T>> =
        map { node ->
            if (node.id == targetId) {
                node.copy(children = (node.children + nodeToAdd).toMutableList()) // Add node under new parent
            } else {
                node.copy(
                    children =
                        node.children.addNode(
                            nodeToAdd,
                            targetId,
                        ),
                )
            }
        }.toMutableList()

    /**
     * Finds a node in the tree by its ID.
     *
     * @param id The ID of the node to find.
     * @return The node if found, null otherwise.
     */
    fun <T> List<Node<T>>.findNode(id: Int): Node<T>? {
        for (node in this) {
            if (node.id == id) return node
            val found = node.children.findNode(id)
            if (found != null) return found
        }
        return null
    }

    /**
     * Finds the closest child node to a given drop position.
     *
     * @param dropPosition The offset where a node is being dropped.
     * @param lazyListState The state of the LazyColumn/LazyRow.
     */

    fun <T> Node<T>.findClosestChild(
        dropPosition: Offset,
        lazyListState: LazyListState,
    ): Node<T>? {
        val visibleNodes =
            lazyListState.layoutInfo.visibleItemsInfo.mapNotNull { item ->
                val node = children.find { it.id == item.key }
                node?.let {
                    node to Offset(item.offset.toFloat(), 0f)
                }
            }

        return visibleNodes
            .minByOrNull { (_, nodeOffset) ->
                (nodeOffset - dropPosition).getDistance()
            }?.first
    }

    /**
     * Checks if a node is a descendant of a potential parent node.
     *
     * @param potentialParent The potential parent node.
     * @param tree The tree of nodes to search within.
     * @return True if the node is a descendant, false otherwise.
     */
    fun <T> Node<T>.isDescendantOf(
        potentialParent: Node<T>,
        tree: List<Node<T>>,
    ): Boolean {
        var current = parentId?.let { tree.findNode(it) }
        while (current != null) {
            if (current.id == potentialParent.id) return true
            current = current.parentId?.let { tree.findNode(it) }
        }
        return false
    }

    fun <T> Node<T>.isDirectDescendantOf(
        potentialParent: Node<T>,
        tree: List<Node<T>>,
    ): Boolean {
        val directParent = parentId?.let { tree.findNode(it) }
        return directParent?.id == potentialParent.id
    }
}
