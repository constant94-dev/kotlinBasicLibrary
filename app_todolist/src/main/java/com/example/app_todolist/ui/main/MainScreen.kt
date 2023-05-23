package com.example.app_todolist.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.app_todolist.R
import com.example.app_todolist.ui.main.components.TodoItem
import kotlinx.coroutines.launch


@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    /*
    * 'remember' 데이터를 기억하기 위함
    * 'rememberSaveable' 화면을 회전해도 데이터를 기억하기 위함
    * */
    var text by rememberSaveable { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current // 현재 사용중인 '키보드' 제어 변수

    /*
    * 'rememberScaffoldState()' 함수는 현재 사용중인 compose 버전에서 deprecated 되었고
    *  아래와 같이 사용해야 한다
    * */
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "오늘 할일") }
            )
        }, // 'topBar' end
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(60.dp)) // 'Scaffold' 와 'OutlinedTextField' 겹치기 때문에 사용하는 강제 여백
            OutlinedTextField(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                value = text,
                onValueChange = {
                    text = it
                },
                placeholder = { Text(text = "할 일") },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = ""
                    )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), // '모바일 키보드' 엔터 부분
                keyboardActions = KeyboardActions(onDone = {
                    viewModel.addTodo(text)
                    text = ""
                    keyboardController?.hide() // 키보드 숨기기
                }) // '모바일 키보드' 엔터 이벤트
            ) // 'OutlinedTextField' function end

            Divider() // '모바일' 화면에 표시되는 가로 긴 줄

            // 'Column' 에서 제공하는 파라미터 중 'content' 는 함수 형태로 작성할 수 있다.
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // 'item' 끼리의 간격
            ) {
                items(viewModel.items.value) { todoItem ->
                    Column {
                        TodoItem(
                            todo = todoItem,
                            onClick = { todo ->
                                viewModel.toggle(todo.uid)
                            },
                            onDeleteClick = { todo ->
                                viewModel.delete(todo.uid)

                                // 'showSnackbar' 함수가 'suspend' 이기 때문에 스코프가 필요하다
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "할 일 삭제됨",
                                        actionLabel = "취소",
                                    )

                                    // 'Snackbar' 에 있는 액션을 동작했다면
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.restoreTodo() // 작업을 이전으로 되돌린다
                                    }
                                }

                            }
                        ) // 'TodoItem' 부모 레이아웃 내부의 들어가는 '할일' 세부내용 레이아웃
                        Spacer(modifier = Modifier.height(16.dp)) // 'item' 내부의 상단 여백
                        Divider(color = Color.Black, thickness = 1.dp)// 'item' 내부의 하단 구분 선
                    } // 자식 'Column' function end
                } // 'items' function end
            } // 'LazyColumn' function end
        } // 부모 'Column' function end
    } // 'Scaffold' function end
}