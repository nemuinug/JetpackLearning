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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// データクラス : ヘッダーのセクション情報を保持
data class Headtitle(val title: String)

// Composable関数 : メイン画面のUIを構築
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainComponent(todoDao: TodoDao) {
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf<Todo?>(null) }
    val todoList by todoDao.getAll().collectAsState(initial = emptyList())
    val sectionList by todoDao.getAllSections().collectAsState(initial = emptyList()) // セクションリスト取得

    var carentNum by remember { mutableStateOf(0) } // 選択されたセクション番号
    var showDialog by remember { mutableStateOf(false) } // ダイアログ表示状態
    val scrollListState = rememberLazyListState()

    //最初のセクションで初期値を挿入
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            val sectionId = initialAccess(todoDao) ?: return@launch
            carentNum = sectionId // 例のセクション番号を設定
        }
    }


    LaunchedEffect(sectionList) {
        if (sectionList.isNotEmpty() && carentNum == 0) {
            carentNum = sectionList.first().todoSectionNum
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            ScrollList(
                listState = scrollListState,
                todoList = todoList,
                onItemClicked = { /* アイテムクリック処理 */ },
                onSectionSelected = { selectedNum ->
                    carentNum = selectedNum // `carentNum` を更新
                },
                sectionList = sectionList,
                carentNum = carentNum // // 現在のセクション番号を渡す
            )

            FloatingButtons(
                onAddClick = {
                    scope.launch(Dispatchers.IO) {
                        todoDao.insert(
                            Todo(
                                sectionNum = carentNum,
                                title = "新しいTodo",
                                text = "詳細",
                                onChecked = false,
                                tag = "タグ",
                                quantity = 0,
                                onDeleted = false
                            )
                        )
                    }
                },
                onDeleteClick = {
                    selectedItem?.let { todo ->
                        scope.launch(Dispatchers.IO) {
                            todoDao.delete(todo)
                        }
                    }
                },
                onListAdd = { showDialog = true },
                onSearchClick = {
                    // 検索処理（例: 検索ダイアログを開く）
                    println("検索ボタンがクリックされました") //todo 検索処理
                },
            )
        }
    }

    // **`ModalBottomSheet` を `FloatingButtons` の外に移動**
    if (selectedItem != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedItem = null },
            sheetState = sheetState
        ) {
            DetailComponent(selectedItem!!)
        }
    }

    // **Compose_plus.kt の `AddSectionDialog` を呼び出す**
    AddSectionDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = { inputText ->
            scope.launch(Dispatchers.IO) {
                todoDao.insertSection(
                    TodoSection(
                        todoSectionTitle = inputText,
                        todoOnDeleted = false
                    )
                )
            }
        }
    )
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
    onItemClicked: (Todo) -> Unit,
    onSectionSelected: (Int) -> Unit,
    sectionList: List<TodoSection>,
    carentNum: Int
) {
    val filteredTodoList by remember(todoList, carentNum) {
        mutableStateOf(todoList.filter { it.sectionNum == carentNum })
    }

    LazyColumn(state = listState) {
        item {
            AppTopBar()
        }

        stickyHeader {
            HeadtitleList(
                headtitles = sectionList,
                carentNum = carentNum,
                onItemSelected = { selectedNum ->
                    onSectionSelected(selectedNum) // `carentNum` を更新
                },
                listState = listState
            )
        }

        items(filteredTodoList) { todo ->
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Blue)) { // タイトルを青色に変更
                        append(todo.title)
                    }
                    append(": ${todo.text}") // テキスト部分はそのまま
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .clickable { onItemClicked(todo) },
                color = Color.Black // デフォルトの文字色
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

