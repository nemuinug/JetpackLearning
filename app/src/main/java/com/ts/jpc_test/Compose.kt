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
import kotlinx.coroutines.withContext

// データクラス : ヘッダーのセクション情報を保持
data class Headtitle(val title: String)

// Composable関数 : メイン画面のUIを構築
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainComponent(sectionDao: TodoSectionDao, todoDao: TodoDao) {
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf<Todo?>(null) }
    val todoList by todoDao.getAll().collectAsState(initial = emptyList())
    val sectionListState = sectionDao.getAllSections().collectAsState(initial = emptyList())
    val sectionList = sectionListState.value.filter { !it.isTodoDeleted } // フィルタリング
    var carentNum by remember { mutableStateOf(0) } // 選択されたセクション番号
    var canShowDialog by remember { mutableStateOf(false) } // ダイアログ表示状態
    val scrollListState = rememberLazyListState()   // LazyColumn用
    val headerRowListState = rememberLazyListState()  // LazyRow用（新規に追加）

    // 最初のセクションで初期値を挿入
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            val sectionId = initialAccess(sectionDao, todoDao) ?: return@launch
            withContext(Dispatchers.Main) {
                carentNum = sectionId // メインスレッドで更新
            }
        }
    }

    // 削除後に移動するため、最初の `carentNum` を設定
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
                onItemClicked = { todo ->
                    selectedItem = todo // クリックしたアイテムをセット
                },
                onSectionSelected = { selectedNum ->
                    carentNum = selectedNum // `carentNum` を更新
                },
                sectionList = sectionList,
                carentNum = carentNum, // 現在のセクション番号を渡す
                headerRowListState = headerRowListState
            )

            FloatingButtons(
                todoDao = todoDao,
                sectionDao = sectionDao,
                carentNum = carentNum,
                onCarentNumChange = { newCarentNum -> carentNum = newCarentNum },
                onAddClick = {
                    scope.launch(Dispatchers.IO) {
                        todoDao.insert(
                            Todo(
                                sectionNum = carentNum,
                                title = "新しいTodo",
                                text = "詳細",
                                isChecked = false,
                                tag = "タグ",
                                quantity = 0,
                                isDeleted = false
                            )
                        )
                    }
                },
                onDeleteClick = { // ここで削除処理を追加
                    scope.launch(Dispatchers.IO) {
                        sectionDao.deleteLogicallySection(carentNum) // 論理削除

                        // **削除後の遷移先を決定**
                        val updatedSections =
                            sectionDao.getAllSectionsNow().filter { !it.isTodoDeleted }

                        val currentIndex =
                            updatedSections.indexOfFirst { it.todoSectionNum == carentNum }

                        if (updatedSections.isNotEmpty()) {
                            val newCarentNum = if (currentIndex > 0) {
                                updatedSections[currentIndex - 1].todoSectionNum // 左のグループに移動
                            } else {
                                updatedSections.first().todoSectionNum // 先頭のグループに移動
                            }
                            withContext(Dispatchers.Main) {
                                carentNum = newCarentNum // `carentNum` を更新
                            }
                        }
                    }
                },
                onListAdd = { canShowDialog = true },
                onSearchClick = { println("検索ボタンがクリックされました") }
            )
        }
    }
    if (selectedItem != null) {
        DetailComponent(
            todo = selectedItem!!,
            onDismiss = { selectedItem = null } // 閉じる処理
        )
    }

    // **Compose_plus.kt の `AddSectionDialog` を呼び出す**
    AddSectionDialog(
        showDialog = canShowDialog,
        onDismiss = { canShowDialog = false },
        onConfirm = { inputText ->
            scope.launch(Dispatchers.IO) {
                sectionDao.insert(
                    TodoSection(
                        todoSectionTitle = inputText,
                        isTodoDeleted = false
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
    val scope = rememberCoroutineScope()
    var showSettings by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) } //　 メニューの表示・非表示を管理

    Column {
        //　 `TopAppBar` の見た目を統一
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showMenu = true }) { //　 メニューアイコンでポップアップを開く
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
                    IconButton(onClick = { showSettings = true }) { //　 設定のポップアップを開く
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "設定")
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color(0xFF228B22), thickness = 1.dp)

        //　 メインコンテンツのプレースホルダー
        Box(modifier = Modifier.fillMaxSize()) {
            // ここにスクロールリストなどを配置
        }
    }

    //　 メニュー (ModalBottomSheet) の実装
    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = { showMenu = false }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("メニュー", style = MaterialTheme.typography.headlineSmall)
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                //　 メニューのリスト
                listOf("ホーム", "タスク一覧", "設定", "ログアウト").forEach { item ->
                    Text(
                        text = item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showMenu = false //　 メニューを閉じる
                                // ここに各メニューの処理を記述
                            }
                            .padding(vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // 設定 (ModalBottomSheet) の実装
    if (showSettings) {
        ModalBottomSheet(
            onDismissRequest = { showSettings = false }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("設定", style = MaterialTheme.typography.headlineSmall)
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("テーマ設定", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = true, onCheckedChange = { /* 設定変更処理 */ })
                Spacer(modifier = Modifier.height(8.dp))
                Text("通知設定", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = false, onCheckedChange = { /* 設定変更処理 */ })
            }
        }
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
    carentNum: Int,
    headerRowListState: LazyListState
) {
    val filteredTodoList by remember(todoList, carentNum) {
        derivedStateOf { todoList.filter { it.sectionNum == carentNum } }
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
                listState = headerRowListState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailComponent(todo: Todo, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true) // 修正

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "タイトル: ${todo.title}", style = MaterialTheme.typography.titleLarge)
            Text(text = "内容: ${todo.text}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "タグ: ${todo.tag}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                onDismiss() // 閉じる処理
            }) {
                Text("閉じる")
            }
        }
    }
}



// 半透明のオーバーレイ + 左側からスライドするメニュー
@Composable
fun OverlayMenu(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // 半透明の背景
            .clickable(onClick = onDismiss), // クリックで閉じる
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .width(250.dp)
                .fillMaxHeight()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column {
                Text("メニュー", style = MaterialTheme.typography.headlineSmall)
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                listOf("ホーム", "タスク一覧", "設定", "ログアウト").forEach { item ->
                    Text(
                        text = item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* メニューの処理 */ }
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// 半透明のオーバーレイ + 右側からスライドする設定画面
@Composable
fun OverlaySettings(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) // 半透明の背景
            .clickable(onClick = onDismiss), // クリックで閉じる
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column {
                Text("設定", style = MaterialTheme.typography.headlineSmall)
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("テーマ設定", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = true, onCheckedChange = { /* 設定変更処理 */ })
                Spacer(modifier = Modifier.height(8.dp))
                Text("通知設定", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = false, onCheckedChange = { /* 設定変更処理 */ })
            }
        }
    }
}