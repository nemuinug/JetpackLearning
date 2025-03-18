package com.ts.jpc_test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,// 自動でIDを増やす
    @ColumnInfo(name = "section") val section: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "onChecked") val onChecked: Boolean,
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "onDeleted") val onDeleted: Boolean
)


