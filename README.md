# MultilevelTree-Compose

A Jetpack Compose library for creating drag-and-droppable multilevel tree structures with ease.

## Features
- Multilevel nested tree support
- Drag and drop functionality
- Expand/collapse nodes
- Customizable UI

## Installation

Add the dependency in your project's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.bhavikmaradiya:multileveltree-compose:latest-version")
}
```

## Usage

### 1. Define Your Data In Node Model

```kotlin
data class Node(
    val id: Int,
    val parentId: Int? = null,
    val data: String
)
```

### 2. Create the Tree Structure

```kotlin
val nodes = listOf(
    Node(1, data = "Root"),
    Node(2, parentId = 1, data = "Child 1"),
    Node(3, parentId = 1, data = "Child 2")
)
```

### 3. Implement `TreeView`

```kotlin
TreeView(nodes, modifier) { node ->
    Text(text = node.data, modifier = Modifier.padding(8.dp))
}
```

## Customization
- Customize node appearance with your own Composable UI
- Configure drag/drop sensitivity
- Define rules for allowed moves

## License

```
MIT License

Copyright (c) 2025 Bhavik Maradiya

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software...
```
