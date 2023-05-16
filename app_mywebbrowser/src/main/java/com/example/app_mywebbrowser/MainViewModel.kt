package com.example.app_mywebbrowser

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val url = mutableStateOf("https://www.google.com")

    private val _undoSharedFlow = MutableSharedFlow<Boolean>() // 동일한 undo 데이터가 발생하게 함
    val undoSharedFlow = _undoSharedFlow.asSharedFlow() // 수정이 안되는 sharedFlow 로 변경

    private val _redoSharedFlow = MutableSharedFlow<Boolean>() // 동일한 redo 데이터가 발생하게 함
    val redoSharedFlow = _redoSharedFlow.asSharedFlow() // 수정이 안되는 sharedFlow 로 변경

    fun undo() {
        viewModelScope.launch {
            _undoSharedFlow.emit(true)
        }
    }

    fun redo() {
        viewModelScope.launch {
            _redoSharedFlow.emit(true)
        }
    }
}