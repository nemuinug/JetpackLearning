package com.ts.jpc_test

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// `Headtitle` のデータクラス
data class Headtitle(val title: String)

//ExperimentalFoundationApiとExperimentalMaterial3Apiを有効化し、StickyHeaderListとmaterial3を使用
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 640) //リアルタイムビュー
@Composable
fun StickyHeaderList() {
    val listState = rememberLazyListState() // スクロール状態を記憶

    // ヘッダー部分のダミーデータを作成
    val headtitles = listOf(
        Headtitle("セクション 1"),
        Headtitle("セクション 2"),
        Headtitle("セクション 3"),
        Headtitle("セクション 4")
    )

    Box(
        modifier = Modifier.fillMaxSize() // 画面全体をカバー
    ) {
        // スクロール可能なリスト
        LazyColumn(state = listState) {
            item {
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
                Divider(color = Color.Black, thickness = 1.dp)
            }

            // スクロールしても固定されるヘッダー
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.padding(1.dp)) {
                        HeadtitleList(headtitles)
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
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    color = Color.Black
                )
            }
        }
        FloatingButtons()
    }
}


// 横スクロール可能な見出しリスト
@Composable
fun HeadtitleList(headtitles: List<Headtitle>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0)),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(headtitles) { index, headtitle ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = headtitle.title,
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 8.dp)
                        .background(Color.Transparent, shape = RoundedCornerShape(8.dp)) // 背景を透明に
                        .padding(2.dp)
                        .wrapContentSize(align = Alignment.Center), // 縦横どちらも中央配置
                            style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize.times(0.7f),
                        color = Color.Black, // 黒文字
                        textAlign = TextAlign.Center
                    )
                )
                // 最後のアイテムの後に Divider を描画しない
                if (index < headtitles.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(8.dp) // Divider の高さを調整
                            .align(Alignment.CenterVertically),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingButtons() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // 画面全体をカバー
        contentAlignment = Alignment.BottomEnd // 右下を基準点に配置
    ) {
        val smallButtonSize = 48.dp // 小さいボタンのサイズ
        val bigButtonSize = 80.dp // 大きいボタンのサイズ
        val distance = bigButtonSize / 2 + smallButtonSize / 2 + 16.dp // 間隔を設定

        // 小さいボタン（真上）
        FloatingActionButton(
            onClick = { /* 真上のボタンの処理 */ },
            modifier = Modifier
                .size(smallButtonSize)
                .align(Alignment.BottomEnd)
                .offset(y = -distance), // 真上に配置
            shape = RoundedCornerShape(50),
            containerColor = Color(0xFF1E90FF) // 明るめのブルー
        ) {
            Icon(Icons.Filled.Search, contentDescription = "検索", tint = Color.White)
        }

        // 小さいボタン（真横）
        FloatingActionButton(
            onClick = { /* 真横のボタンの処理 */ },
            modifier = Modifier
                .size(smallButtonSize)
                .align(Alignment.BottomEnd)
                .offset(x = -distance* 0.8f,y = -distance * 0.8f), // 真左に配置
            shape = RoundedCornerShape(50),
            containerColor = Color(0xFF9DC183)
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "設定", tint = Color.White)
        }

        // 小さいボタン（真横）
        FloatingActionButton(
            onClick = { /* 真横のボタンの処理 */ },
            modifier = Modifier
                .size(smallButtonSize)
                .align(Alignment.BottomEnd)
                .offset(x = -distance), // 真左に配置
            shape = RoundedCornerShape(50),
            containerColor = Color(0xFFFFB6C1)
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "設定", tint = Color.White)
        }

        // 中央の大きなボタン（プラスマーク）
        FloatingActionButton(
            onClick = { /* 大ボタンの処理 */ },
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



