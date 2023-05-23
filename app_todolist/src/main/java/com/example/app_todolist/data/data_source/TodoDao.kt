package com.example.app_todolist.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app_todolist.domain.model.Todo
import kotlinx.coroutines.flow.Flow

// 'model' 의 만들어놓은 'Todo' 객체를 이용한 데이터 접근 객체
@Suppress("AndroidUnresolvedRoomSqlReference")
@Dao
interface TodoDao {

    @Query("SELECT * FROM todo ORDER BY date DESC")
    fun todos(): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 만약 동일한 'id'가지는 'todo' 객체를 insert 하면 충돌하지 않게 대체하겠다
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}