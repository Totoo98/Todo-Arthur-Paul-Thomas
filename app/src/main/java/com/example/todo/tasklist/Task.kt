package com.example.todo.tasklist

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Task(
    @kotlinx.serialization.SerialName("id")
    val id: String,
    @kotlinx.serialization.SerialName("content")
    val title: String,
    @kotlinx.serialization.SerialName("description")
    val description: String = ""
):Serializable