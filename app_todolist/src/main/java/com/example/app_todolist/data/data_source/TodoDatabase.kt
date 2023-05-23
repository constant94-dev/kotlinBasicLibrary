package com.example.app_todolist.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.app_todolist.domain.model.Todo

/*
* 'entity' 가 여러개 존재하면 콤마(,) 를 사용해 이어서 작성
* 'version' 은 테이블의 구조가 변경되었을 때 업그레이드 처리를 위한 정보
* 예를들어, 현재 작성된 Todo 객체의 구조가 변경되면 App 을 삭제하고 실행하던지 version 을 올려주어야 한다
* */
@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao // 인터페이스로 만든 'TodoDao' 객체가 자동으로 얻어지는 기능을 한다
}