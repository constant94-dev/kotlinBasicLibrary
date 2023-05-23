package com.example.app_todolist.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity
data class Todo(
    val title: String,
    val date: Long = Calendar.getInstance().timeInMillis,
    val isDone: Boolean = false,
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0 // 'id' 값을 생성자의 작성하게되면 동적으로 정보를 설정할 수 없어 동적설정을 위해 생성자 외부의 설정한다
}