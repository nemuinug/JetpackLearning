package com.ts.jpc_test

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import kotlinx.coroutines.launch

// データクラス : ヘッダーのセクション情報を保持
data class Headtitle(val title: String)

// Composable関数 : メイン画面のUIを構築
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainComponent() {
    val listState = rememberLazyListState() // リストのスクロール状態を管理
    val sheetState = rememberModalBottomSheetState() // ボトムシートの状態を管理
    val scope = rememberCoroutineScope() // （並行処理）を管理するためのスコープ
    var selectedItem by remember { mutableStateOf<String?>(null) } // 選択されたアイテムを記憶

    // ヘッダー用のダミーデータ
    val headtitles = listOf(
        Headtitle("セクション 1"),
        Headtitle("セクション 2"),
        Headtitle("セクション 3"),
        Headtitle("セクション 4")
    )

    // 画面全体をカバーするコンテナ
    Box(modifier = Modifier.fillMaxSize()) {
        // スクロール可能なリスト (LazyColumn)
        LazyColumn(state = listState) {
            // トップバー (アプリのタイトルとメニュー)
            item {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // メニューボタン
                            IconButton(onClick = { /* メニュー処理 */ }) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "メニュー"
                                )
                            }
                            Spacer(modifier = Modifier.weight(4f)) // 中央に余白

                            // タイトル
                            Text(
                                "Todoリスト",
                                style = MaterialTheme.typography.headlineLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.weight(1f)) // 右側に余白

                            // 検索ボタン
                            IconButton(onClick = { /* 検索の処理 */ }) {
                                Icon(imageVector = Icons.Filled.Search, contentDescription = "検索")
                            }
                            // 設定ボタン
                            IconButton(onClick = { /* 設定の処理 */ }) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "設定"
                                )
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp)) // 余白
                Divider(color = Color(0xFF228B22), thickness = 1.dp) // 緑色の境界線
            }

            // スクロールしても固定されるヘッダー
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color(0xFF9DC183)), // ヘッダーの背景色
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.padding(1.dp)) {
                        HeadtitleList(headtitles) // ヘッダーリストの描画
                    }
                }
            }

            // リストの内容
            items(20) { index ->
                Text(
                    text = "アイテム $index",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp)) // 白背景 & 角丸
                        .padding(16.dp)
                        .clickable {
                            selectedItem = "アイテム $index" // タップされたアイテムを記録
                            scope.launch { sheetState.show() } // ボトムシートを表示
                        },
                    color = Color.Black
                )
            }
        }

        // フローティングボタン (右下)
        FloatingButtons()

        // 詳細画面 (ボトムシート)
        if (selectedItem != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedItem = null }, // 画面外タップで閉じる
                sheetState = sheetState
            ) {
                DetailComponent(selectedItem!!) {
                    selectedItem = null // 閉じる処理
                }
            }
        }
    }
}

// 詳細画面 (ボトムシート)
@Composable
fun DetailComponent(selectedItem: String, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp) // 高さを増やして、中央まで表示
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 選択したアイテムの詳細
        Text(
            text = selectedItem,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp)) // 余白

        // 閉じるボタン
        Button(onClick = onClose) {
            Text("閉じる")
        }
    }
}
