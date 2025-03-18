package com.ts.jpc_test

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// データクラス : ヘッダーのセクション情報を保持
data class Headtitle(val title: String)

// Composable関数 : メイン画面のUIを構築
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainComponent(todoDao: TodoDao) {
    val listState = rememberLazyListState() // リストのスクロール状態を管理
    val sheetState = rememberModalBottomSheetState() // ボトムシートの状態を管理
    val scope = rememberCoroutineScope() // 並行処理を管理するためのスコープ
    var selectedItem by remember { mutableStateOf<Todo?>(null) } // 選択されたアイテムを記憶
    val todoList by remember { todoDao.getAll() }.collectAsState(initial = emptyList())

    // ボタン押下時の処理を定義
    val onAddClick: () -> Unit = {
        scope.launch(Dispatchers.IO) { // I/O スレッドで処理
            todoDao.insert(Todo(section = "1", title = "新しいTodo", text = "詳細", onChecked = false, tag = "タグ", quantity = 0, onDeleted = false))
        }
    }

    val onDeleteClick: (Todo) -> Unit = { todo ->
        scope.launch(Dispatchers.IO) { // I/O スレッドで処理
            todoDao.delete(todo)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            ScrollList(
                listState = listState,
                todoList = todoList,
                onItemClicked = { todo ->
                    selectedItem = todo
                    scope.launch { sheetState.show() }
                }
            )

            // ボタンの処理を渡す
            FloatingButtons(
                onAddClick = onAddClick,
                onDeleteClick = { selectedItem?.let(onDeleteClick) }
            )

            if (selectedItem != null) {
                ModalBottomSheet(
                    onDismissRequest = { selectedItem = null },
                    sheetState = sheetState
                ) {
                    DetailComponent(selectedItem!!)
                }
            }
        }
    }
}


// トップバーのComposable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    Column {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* メニュー処理 */ }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "メニュー")
                    }
                    Spacer(modifier = Modifier.weight(4f))

                    Text(
                        "Todoリスト",
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { /* 検索の処理 */ }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "検索")
                    }
                    IconButton(onClick = { /* 設定の処理 */ }) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "設定")
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color(0xFF228B22), thickness = 1.dp)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollList(
    listState: LazyListState,
    todoList: List<Todo>,
    onItemClicked: (Todo) -> Unit
) {
    LazyColumn(state = listState) {
        // トップバー
        item {
            AppTopBar()
        }

        // 固定ヘッダー
        stickyHeader {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF9DC183)), // ヘッダーの背景色
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Todoリスト",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )
            }
        }

        // Todo リスト
        items(todoList) { todo ->
            Text(
                text = "${todo.title}: ${todo.quantity}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .clickable { onItemClicked(todo) }, // `Todo` を渡す
                color = Color.Black
            )
        }
    }
}


// 詳細画面 (ボトムシート)
@Composable
fun DetailComponent(todo: Todo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "タイトル: ${todo.title}", style = MaterialTheme.typography.titleLarge)
        Text(text = "内容: ${todo.text}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "タグ: ${todo.tag}", style = MaterialTheme.typography.bodySmall)
    }
}

