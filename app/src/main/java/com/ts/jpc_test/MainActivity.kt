package com.ts.jpc_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ts.jpc_test.ui.theme.JPC_testTheme

// MainActivity (アプリのエントリーポイント)
class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var todoDao: TodoDao
    private lateinit var sectionDao: TodoSectionDao

    // onCreate() メソッド:アクティビティが作成されたときに実行
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 親クラスのonCreateを呼び出し

        // Room データベースのインスタンスを取得
        database = AppDatabase.getDatabase(this)
        todoDao = database.todoDao()
        sectionDao = database.sectionDao()
        enableEdgeToEdge() // フルスクリーン(エッジからエッジまで描画)を有効化

        setContent { // Jetpack ComposeのUI
            JPC_testTheme { // アプリのテーマを適用
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // ScaffoldはMaterial Design のレイアウト構造

                    Column(modifier = Modifier.padding(innerPadding)) {
                        // メインのUIを表示
                        MainComponent(sectionDao = sectionDao, todoDao = todoDao)
                    }
                }
            }
        }
    }
}