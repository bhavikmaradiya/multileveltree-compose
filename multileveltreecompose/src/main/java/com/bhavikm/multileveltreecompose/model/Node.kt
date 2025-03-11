package com.bhavikm.multileveltreecompose.model

data class Node<T>(
    val id: Int,
    var parentId: Int? = null,
    var data: T,
    var children: MutableList<Node<T>> = mutableListOf()
)