package com.example.app_todolist.domain.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app_todolist.data.repository.TodoRepositoryImpl
import com.example.app_todolist.domain.repository.TodoRepository
import com.example.app_todolist.ui.main.MainViewModel

/*
* 특정 상황에서 내가 원하는 ViewModel 을 사용하려면 이러한 Factory 클래스를 별도로 만들어야 한다
* */
class TodoAndroidViewModelFactory(
    private val application: Application,
    private val repository: TodoRepository = TodoRepositoryImpl(application) // 'repository' 변수의 타입과 기본값을 설정한다
) :
    ViewModelProvider.AndroidViewModelFactory(application) {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 'MainViewModel' 을 만드는 경우 어떤 작업을 해줄 것인가
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application = application, repository) as T
        }
        return super.create(modelClass) // 위 경우를 제외하면 일반적인 기능으로 제공하겠다
    }
}