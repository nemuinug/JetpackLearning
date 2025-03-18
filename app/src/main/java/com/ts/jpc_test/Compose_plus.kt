package com.ts.jpc_test

import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//縁取り文字を作成するfun
val smallButtonSize = 65.dp // 小さいボタンのサイズ
val bigButtonSize = 100.dp // 大きいボタンのサイズ
val distance = bigButtonSize / 2 + smallButtonSize / 2 + 16.dp // 間隔を設定

@Composable
fun OutlinedText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    stroke: Stroke = Stroke(),
    strokeColor: Color = Color.Transparent,
) {
    var textLayoutResult: TextLayoutResult? by remember {
        mutableStateOf(null)
    }
    Text(
        text = text,
        style = textStyle,
        onTextLayout = {
            textLayoutResult = it
        },
        modifier = modifier
            .drawBehind {
                textLayoutResult?.let {
                    drawText(
                        textLayoutResult = it,
                        drawStyle = stroke,
                        color = strokeColor,
                    )
                }
            }
    )
}

@Composable
fun FloatingButtons(
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSearchClick: () -> Unit,
    onListAdd: () -> Unit
) {
    val smallButtonSize = 56.dp
    val bigButtonSize = 80.dp
    val distance = 80.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        // 小さいボタン（真上）
        FloatingActionButton(
            onClick = onSearchClick, // 検索処理
            modifier = Modifier
                .size(smallButtonSize)
                .align(Alignment.BottomEnd)
                .offset(y = -distance), // 真上に配置
            shape = RoundedCornerShape(50),
            containerColor = Color(0xFF1E90FF) // 明るめのブルー
        ) {
            Icon(Icons.Filled.Search, contentDescription = "検索", tint = Color.White)
        }

        // 小さいボタン（斜め左上）
        FloatingActionButton(
            onClick = onListAdd, // 設定処理
            modifier = Modifier
                .size(smallButtonSize)
                .align(Alignment.BottomEnd)
                .offset(x = -distance * 0.8f, y = -distance * 0.8f), // 斜め左上に配置
            shape = RoundedCornerShape(50),
            containerColor = Color(0xFF9DC183)
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "横スクロールアイテム追加",
                tint = Color.White
            )
        }

        // 小さいボタン（真左）
        FloatingActionButton(
            onClick = onDeleteClick, // 削除処理
            modifier = Modifier
                .size(smallButtonSize)
                .align(Alignment.BottomEnd)
                .offset(x = -distance), // 真左に配置
            shape = RoundedCornerShape(50),
            containerColor = Color(0xFFFFB6C1) // ピンク
        ) {
            Icon(
                imageVector = Icons.Filled.Delete, // ゴミ箱アイコン
                contentDescription = "削除",
                tint = Color.White
            )
        }

        // 中央の大きなボタン（プラスマーク）
        FloatingActionButton(
            onClick = onAddClick, // 追加処理
            modifier = Modifier
                .size(bigButtonSize)
                .align(Alignment.BottomEnd), // 右下に固定
            shape = RoundedCornerShape(50),
            containerColor = Color(0xFF00BFFF) // 明るい水色
        ) {
            Icon(Icons.Filled.Add, contentDescription = "追加", tint = Color.White)
        }
    }
}


// 横スクロール可能な見出しリスト
@Composable
fun HeadtitleList(
    headtitles: List<TodoSection>,
    carentNum: Int,
    onItemSelected: (Int) -> Unit,
    listState: LazyListState
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF9DC183)),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(16.dp)

    ) {
        itemsIndexed(headtitles) { _, headtitle -> // `index` ではなく `headtitle`
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(50.dp)
                    .clickable {
                        onItemSelected(headtitle.todoSectionNum) // `todoSectionNum` を渡す
                    }
                    .background(
                        Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = headtitle.todoSectionTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 8.dp)
                        .wrapContentSize(align = Alignment.Center),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize.times(0.7f),
                        color = if (carentNum == headtitle.todoSectionNum) Color.Black else Color.White,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}


@Composable
fun AddSectionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") } // 入力テキストを管理

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("新しいセクションを追加") },
            text = {
                Column {
                    Text("セクション名を入力してください:")
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onConfirm(inputText) // 確定時にテキストを渡す
                            inputText = "" // 入力をリセット
                            onDismiss() // ダイアログを閉じる
                        }
                    }
                ) {
                    Text("追加")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("キャンセル")
                }
            }
        )
    }
}

suspend fun initialAccess(todoDao: TodoDao): Int? {
    return withContext(Dispatchers.IO) {
        if (todoDao.getSectionCount() == 0) {
            val exampleSection = TodoSection(
                todoSectionTitle = "例",
                todoOnDeleted = false
            )
            val exampleSectionId: Long = todoDao.insertSection(exampleSection)

            // 直後にデータベースから ID を確認
            val updatedSections = todoDao.getAllSectionsNow()
            val exampleSectionNum =
                updatedSections.find { it.todoSectionTitle == "例" }?.todoSectionNum
                    ?: return@withContext null

            todoDao.insertTodos(
                listOf(
                    Todo(
                        sectionNum = exampleSectionNum,
                        title = "グループを追加",
                        text = "緑色の＋ボタン",
                        onChecked = false,
                        tag = "デフォルト",
                        quantity = 1,
                        onDeleted = false
                    ),
                    Todo(
                        sectionNum = exampleSectionNum,
                        title = "グループを削除",
                        text = "🗑ボタン",
                        onChecked = false,
                        tag = "デフォルト",
                        quantity = 1,
                        onDeleted = false
                    ),
                    Todo(
                        sectionNum = exampleSectionNum,
                        title = "アイテムを追加",
                        text = "青色の＋ボタン",
                        onChecked = false,
                        tag = "デフォルト",
                        quantity = 1,
                        onDeleted = false
                    ),
                    Todo(
                        sectionNum = exampleSectionNum,
                        title = "アイテムを削除",
                        text = "アイテムをスワイプ",
                        onChecked = false,
                        tag = "デフォルト",
                        quantity = 1,
                        onDeleted = false
                    ),
                    Todo(
                        sectionNum = exampleSectionNum,
                        title = "アイテムを検索",
                        text = "🔍ボタン",
                        onChecked = false,
                        tag = "デフォルト",
                        quantity = 1,
                        onDeleted = false
                    ),
                )
            )

            return@withContext exampleSectionNum
        } else {
            return@withContext null
        }
    }
}



