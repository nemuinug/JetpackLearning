package com.ts.jpc_test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,// 自動でIDを増やす
    @ColumnInfo(name = "sectionNum") val sectionNum: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "onChecked") val onChecked: Boolean,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "onDeleted") val onDeleted: Boolean
)

@Entity(tableName = "todo_section_table")
data class TodoSection(
    @PrimaryKey(autoGenerate = true) val todoSectionNum: Int = 0,// 自動でIDを増やす
    @ColumnInfo(name = "todoSectionTitle") val todoSectionTitle: String,// 横リストアイテムタイトル
    @ColumnInfo(name = "todoOnDeleted") val todoOnDeleted: Boolean
)


