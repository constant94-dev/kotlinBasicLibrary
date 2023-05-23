package com.example.app_todolist.data.repository

import android.app.Application
import androidx.room.Room
import com.example.app_todolist.data.data_source.TodoDatabase
import com.example.app_todolist.domain.model.Todo
import com.example.app_todolist.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

/*
* 'TodoRepositoryImpl' 클래스에서 'Application' 객체를 가지고 있는 이유는 앞으로 만들어질 'ViewModel' 에서도 'AndroidViewModel' 을 사용하게 되어
* 'Application' 객체를 사용하는데 이 때 사용되는 'Application' 객체를 여기서도 사용해주기 위해 필요하다
* 'TodoRepositoryImpl' 클래스에 역할은 타겟 데이터베이스의 실제 SQL 명령을 전달하는 구현체이다.
* */
class TodoRepositoryImpl(application: Application) : TodoRepository {
    private val db = Room.databaseBuilder(
        application,
        TodoDatabase::class.java,
        "todo-db",
    ).build()

    override fun observeTodos(): Flow<List<Todo>> {
        return db.todoDao().todos()
    }

    override suspend fun addTodo(todo: Todo) {
        return db.todoDao().insert(todo)
    }

    override suspend fun updateTodo(todo: Todo) {
        return db.todoDao().update(todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        return db.todoDao().delete(todo)
    }
}