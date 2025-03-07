package com.ts.jpc_test

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

//ExperimentalFoundationApiとExperimentalMaterial3Apiを有効化し、StickyHeaderListとmaterial3を使用
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StickyHeaderList() {
    val listState = rememberLazyListState() //スクロールを記憶
    LazyColumn(state = listState) //スクロール可能コンポーネント
    {
        item {
            TopAppBar( //上部バー
                title = { //以下タイトルの設定
                    Row( //横に並べる
                        modifier = Modifier
                            .fillMaxWidth() //幅を指定
                            .padding(8.dp), //余白の設定
                            verticalAlignment = Alignment.CenterVertically  // 縦方向を中央揃え
                    ) {
                        // 空の Spacer で左側のスペースを確保
                        Spacer(modifier = Modifier.weight(1.5f)) //ボタンの分だけスペースを増設
                        Text(
                            "Todoリスト",
                            style = MaterialTheme.typography.headlineLarge, //フォント
                            maxLines = 1, //改行なし
                            overflow = TextOverflow.Ellipsis //オーバーフロー（長文化)を省略
                        )
                        // 右端にアイコン（Spacer で中央に寄せる）
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { /* 検索の処理 */ }) { // TODO: ボタンの処理は未実装
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "検索"
                            )
                        }
                    }

                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        //ヘッダー　スクロールしても固定で表示される
        stickyHeader {
            Box( // Boxでテキストの中央揃え
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4A5D23)) // モスグリーン
                    .padding(16.dp),
                contentAlignment = Alignment.Center // Box の中央に配置
            ) {
                Text(
                    text = "セクション 1",
                    color = Color.White,
                    textAlign = TextAlign.Center // テキストを中央揃え
                )
            }
        }
    }
}
