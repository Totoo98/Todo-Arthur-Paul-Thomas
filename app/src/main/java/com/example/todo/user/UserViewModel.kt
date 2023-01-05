package com.example.todo.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.data.Api
import com.example.todo.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val webService = Api.userWebService

    public val userStateFlow = MutableStateFlow<User>(User("Default email", "Default name"))

    fun refresh() {
        viewModelScope.launch {
            val response = webService.fetchUser() // Call HTTP (opération longue)
            if (!response.isSuccessful) { // à cette ligne, on a reçu la réponse de l'API
                Log.e("Network", "Error: ${response.message()}")
                return@launch
            }
            val fetchedUser = response.body()!!
            userStateFlow.value = fetchedUser // on modifie le flow, ce qui déclenche ses observers
        }
    }

    fun editAvatar(user: User) {

    }
}