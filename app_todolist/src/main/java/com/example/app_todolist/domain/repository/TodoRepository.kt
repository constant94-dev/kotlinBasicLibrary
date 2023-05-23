package com.example.app_todolist.domain.repository

import com.example.app_todolist.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun observeTodos(): Flow<List<Todo>> // 실제 DB 를 경유해서 데이터를 얻는 것을 지원

    suspend fun addTodo(todo: Todo) // '할 일' 추가
    suspend fun updateTodo(todo: Todo) // '할 일' 수정
    suspend fun deleteTodo(todo: Todo) // '할 일' 삭제
}