package com.ts.jpc_test

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context


@Database(entities = [Todo::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao //DAO (Data Access Object) を取得するための抽象メソッド。

    companion object {
        @Volatile
        // データベースインスタンスを保持するための変数（Volatile: 複数スレッドからの可視性を保証）
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { // 同期化してスレッドセーフにする
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"// データベース名
                )
                    .fallbackToDestructiveMigration() // スキーマ変更時にデータを削除して再作成
                    .allowMainThreadQueries() // メインスレッドでのクエリを許可
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

