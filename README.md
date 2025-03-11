# MultilevelTree-Compose

![Jetpack Compose](https://img.shields.io/badge/jetpack-compose-%237F52FF.svg?style=for-the-badge&logo=jetpack-compose&logoColor=white) ![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white) ![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.bhavikmaradiya/multileveltreecompose)](https://central.sonatype.com/artifact/io.github.bhavikmaradiya/multileveltreecompose) ![Static Badge](https://img.shields.io/badge/minSdk-21-blue?link=https%3A%2F%2Fgithub.com%2Fbhavikmaradiya%2Fmultileveltree-compose%2Fblob%2Fmain%2Fbuild.gradle.kts)

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
    implementation("io.github.bhavikmaradiya:multileveltreecompose:latest-version")
}
```

## Usage

### 1. Define Your Tree Model

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

## Artifact Information

You can find the library on [Sonatype Central Repository](https://central.sonatype.com/artifact/io.github.bhavikmaradiya/multileveltreecompose).

## License

```
MIT License

Copyright (c) 2025 Bhavik Maradiya

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software...
```
