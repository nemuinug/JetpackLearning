package com.ts.jpc_test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.unit.dp

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

//座標が固定されているボタンアイコン
@Composable
fun FloatingButtons(todoDao: TodoDao) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // 画面全体をカバー
        contentAlignment = Alignment.BottomEnd // 右下を基準点に配置
    ) {
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
                .offset(x = -distance * 0.8f, y = -distance * 0.8f), // 真左に配置
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
            Icon(
                imageVector = Icons.Filled.Delete, // ゴミ箱アイコン
                contentDescription = "削除",
                tint = Color.White
            )
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
                        color = Color.White, // 白文字
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
        // **スクロールの最後に + ボタンを追加**
        item {
            FloatingActionButton(
                onClick = { /* 追加ボタンの処理 */ },
                modifier = Modifier
                    .offset(y = 5.dp)
                    .size(smallButtonSize * 0.7f),
                shape = RoundedCornerShape(50),
                containerColor = Color(0xFFFFFFFF) // 淡いピンク
            ) {
                Icon(
                    modifier = Modifier
                        .padding(8.dp),
                    imageVector = Icons.Filled.Add,
                    contentDescription = "追加",
                    tint = Color.Black
                )
            }
        }
    }
}