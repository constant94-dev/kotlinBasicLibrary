package com.example.app_todolist.ui.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.app_todolist.R
import com.example.app_todolist.domain.model.Todo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodoItem(
    todo: Todo,
    onClick: (todo: Todo) -> Unit = {},
    // onDeleteClick: (id: Int), 해당되는 'id' 값만 제어할 게 아니라 객체 전체를 제어하고 싶을 때는 'todo' 객체 얻어야한다
    onDeleteClick: (todo: Todo) -> Unit = {},
) {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick(todo) },
    ) {
        Row() {
            // 삭제 아이콘
            Icon(painter = painterResource(id = R.drawable.baseline_delete_24),
                contentDescription = null,
                tint = Color(0xFFA51212),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onDeleteClick(todo) }
            ) // 'Icon' function end

            // 내용
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = format.format(Date(todo.date)),
                    color = if (todo.isDone) Color.Gray else Color.Black,
                    style = TextStyle(
                        textDecoration =
                        if (todo.isDone) TextDecoration.LineThrough
                        else TextDecoration.None
                    ) // 'TextDecoration.LineThrough' 는 취소선
                ) // 'Text' function end

                Text(
                    text = todo.title,
                    color = if (todo.isDone) Color.Gray else Color.Black,
                    style = TextStyle(
                        textDecoration =
                        if (todo.isDone) TextDecoration.LineThrough
                        else TextDecoration.None
                    ) // 'TextDecoration.LineThrough' 는 취소선
                ) // 'Text' function end
            } // 자식 'Column' function end

            if (todo.isDone) {
                // 완료 아이콘
                Icon(
                    painter = painterResource(id = R.drawable.baseline_done_24),
                    contentDescription = null,
                    tint = Color(0xFF00BCD4)
                )
            } // 오른쪽 끝에 노출되는 완료 'Icon' function end
        } // 'Row' function end
    } // 부모 'Column' function end
}