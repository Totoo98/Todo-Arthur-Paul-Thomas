package com.example.todo.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todo.detail.ui.theme.TodoThomasBessieresTheme
import com.example.todo.tasklist.Task
import java.util.*

class DetailActivity : ComponentActivity() {

    val onValidate: (Task) -> Unit = { task ->
        intent.putExtra("task", task)
        setResult((RESULT_OK), intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val task = intent.getSerializableExtra("task") as Task?
        setContent {
            TodoThomasBessieresTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Detail(onValidate, task)
                }
            }
        }
    }
}

@Composable
fun Detail(onValidate: (Task) -> Unit, task: Task?) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val id = task?.id ?: UUID.randomUUID().toString()
        val title = task?.title ?: "New task!"
        val description = task?.description ?: ""

        var task by remember { mutableStateOf(Task(
            id = id,
            title = title,
            description = description
        ))}

        Text(text = "New Task", style = MaterialTheme.typography.h3)
        OutlinedTextField(task.title, onValueChange = {task = task.copy(title = it)})
        OutlinedTextField(task.description, onValueChange = {task = task.copy(description = it)})
        Button(onClick = { onValidate(task) }) {
            Text(text = "Add")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    TodoThomasBessieresTheme {
        Detail({}, Task(UUID.randomUUID().toString(), "New task", ""))
    }
}