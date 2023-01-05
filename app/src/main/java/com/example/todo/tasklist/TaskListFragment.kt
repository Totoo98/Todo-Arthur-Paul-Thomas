package com.example.todo.tasklist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.todo.R
import com.example.todo.data.Api
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.databinding.FragmentTaskListBinding
import com.example.todo.detail.DetailActivity
import com.example.todo.user.UserActivity
import com.example.todo.user.UserViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.*

class TaskListFragment : Fragment() {
    private lateinit var binding: FragmentTaskListBinding

    private var adapter = TaskListAdapter()

    private val viewModel: TaskListViewModel by viewModels()
    private val userModel: UserViewModel by viewModels()

    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val task = result.data?.getSerializableExtra("task") as Task?
            if(task != null) {
                viewModel.add(task)
            }
        }
    }

    val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val task = result.data?.getSerializableExtra("task") as Task?
            if(task != null) {
                viewModel.edit(task)
                //adapter.submitList(taskList)
            }
        }
    }

    val showAvatar = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(layoutInflater)

        //adapter.submitList(taskList)
        adapter.onClickDelete = { task ->
            viewModel.remove(task)
            //adapter.submitList(taskList)
        }
        adapter.onClickEdit = { task ->
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("task", task)
            editTask.launch(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter
        binding.floatingActionButton.setOnClickListener() {
            /* val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
            taskList = taskList + newTask
            adapter.submitList(taskList)*/


            val intent = Intent(context, DetailActivity::class.java)
            createTask.launch(intent)
        }

        binding.userImageView.load("https://goo.gl/gEgYUd")

        binding.userImageView.setOnClickListener() {
            val intent = Intent(context, UserActivity::class.java)
            showAvatar.launch(intent)
        }

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                adapter.submitList(newList)
            }
        }

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            userModel.userStateFlow.collect { user ->
                binding.userTextView.text = user.name
                binding.userImageView.load(user.avatar) {
                    error(R.drawable.ic_launcher_background) // image par défaut en cas d'erreur
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.refresh()
        userModel.refresh()
    }
}