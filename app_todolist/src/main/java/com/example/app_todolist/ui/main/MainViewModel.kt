package com.example.app_todolist.ui.main

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_todolist.domain.model.Todo
import com.example.app_todolist.domain.repository.TodoRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
* App 에서 사용되는 실제 데이터 값을 가지고 'View' 에 전달하는 역할을 한다
* */
class MainViewModel(
    application: Application,
    private val todoRepository: TodoRepository, // 데이터 조작을 위해서 필요한 인터페이스 타입인 'TodoRepository'
) : AndroidViewModel(application) {

    private val _items = mutableStateOf(emptyList<Todo>())
    val items: State<List<Todo>> = _items

    // '할일' 삭제 후 되살리기 위한 임시 변수
    private var recentlyDeleteTodo: Todo? = null

    init {
        viewModelScope.launch {
            todoRepository.observeTodos()
                .collect { todos ->
                    _items.value = todos

                }
        }
    } // 초기화 값 세팅 끝

    // '할일' 추가 기능
    fun addTodo(text: String) {
        // 'viewModel' 에서 제공하는 코루틴스코프
        viewModelScope.launch {
            todoRepository.addTodo(Todo(title = text))
        }
    }

    // '할일' 변경 기능
    fun toggle(index: Int) {
        val todo = _items.value.find { todo -> todo.uid == index }
        // 'todo' 객체 null 체크해서 null 이 아닐 때 실행
        todo?.let {
            viewModelScope.launch {
                todoRepository.updateTodo(it.copy(isDone = !it.isDone).apply {
                    uid = it.uid
                }) // 'copy()' 는 'todo' 생성자 값을 복사한다
            } // 'viewModelScope' end
        } // 'let' end
    } // 'toggle' function end

    // '할일' 삭제 기능
    fun delete(index: Int) {
        val todo = _items.value.find { todo -> todo.uid == index } // 삭제할 '할일' 찾기
        todo?.let {
            viewModelScope.launch {
                todoRepository.deleteTodo(it)
                recentlyDeleteTodo = it // 마지막의 삭제했던 객체를 임시변수의 보관한다
            }
        }
    }

    // '할일' 삭제 후 되살리기 기능
    fun restoreTodo() {
        viewModelScope.launch {
            todoRepository.addTodo(
                recentlyDeleteTodo ?: return@launch
            ) // 'recentlyDeleteTodo' null 이라면, 'return@launch' 는 'viewModelScope.launch' 를 취소한다는 의미
            recentlyDeleteTodo = null
        }
    }
}