package com.example.todo.user

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.todo.user.ui.theme.TodoThomasBessieresTheme

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.todo.data.Api
import com.example.todo.data.User
import com.example.todo.tasklist.Task
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.use
import java.io.File
import java.util.*

class UserActivity : ComponentActivity() {
    private val userModel: UserViewModel by viewModels()



    private fun Bitmap.toRequestBody(): MultipartBody.Part {
        val tmpFile = File.createTempFile("avatar", "jpg")
        tmpFile.outputStream().use { // *use* se charge de faire open et close
            this.compress(Bitmap.CompressFormat.JPEG, 100, it) // *this* est le bitmap ici
        }
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = tmpFile.readBytes().toRequestBody()
        )
    }

    private fun Uri.toRequestBody(): MultipartBody.Part {
       return contentResolver.openInputStream(this).use {
            val fileBody = it!!.readBytes().toRequestBody()
             MultipartBody.Part.createFormData(
                name = "avatar",
                filename = "avatar.jpg",
                body = fileBody
            )
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var bitmap: Bitmap? by remember { mutableStateOf(null) }
            var uri: Uri? by remember { mutableStateOf(null) }
            val composeScope = rememberCoroutineScope()

            val captureUri by lazy {
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
            }

            /*val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                bitmap = it
                var requestBody = bitmap?.toRequestBody()
                if(requestBody != null)
                    composeScope.launch {
                        Api.userWebService.updateAvatar(requestBody)
                    }
            }*/

            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    uri = captureUri
                    var requestBody = uri?.toRequestBody()
                    if(requestBody != null)
                        lifecycleScope.launch {
                            Api.userWebService.updateAvatar(requestBody)
                        }
                }
            }

            val choosePicture = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                uri = it
                var requestBody = uri?.toRequestBody()
                if(requestBody != null)
                    GlobalScope.launch {
                        Api.userWebService.updateAvatar(requestBody)
                    }
            }

            val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                if(it)
                    choosePicture.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = bitmap ?: uri,
                    contentDescription = null
                )
                Button(
                    onClick = { takePicture.launch(captureUri) },
                    content = { Text("Take picture") }
                )
                Button(
                    onClick = {
                        requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        },
                    content = { Text("Pick photo") }
                )

                /*val user by
                if(user != null)
                {
                    Text(text = "Edit User Info", style = MaterialTheme.typography.h3)
                    OutlinedTextField(user.email, onValueChange = {user = user.copy(email = it)})
                    OutlinedTextField(user.name, onValueChange = {user = user.copy(name = it)})
                    Button(onClick = { onValidate(task) }) {
                        Text(text = "Edit")
                    }
                }*/
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TodoThomasBessieresTheme {
        Greeting("Android")
    }
}