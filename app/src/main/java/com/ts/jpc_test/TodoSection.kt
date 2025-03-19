package com.ts.jpc_test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todoSection")
data class TodoSection(
    @PrimaryKey(autoGenerate = true) val todoSectionNum: Int = 0,// 自動でIDを増やす
    @ColumnInfo(name = "todoSectionTitle") val todoSectionTitle: String,// 横リストアイテムタイトル
    @ColumnInfo(name = "isTodoDeleted") val todoOnDeleted: Boolean
)
