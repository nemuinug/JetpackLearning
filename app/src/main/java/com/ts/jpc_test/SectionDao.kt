package com.ts.jpc_test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {
    // **TodoSection 用のメソッドを追加**
    @Query("SELECT COUNT(*) FROM todo_section_table")
    suspend fun getSectionCount(): Int // セクション数を取得

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(todoSection: TodoSection): Long

    @Query("SELECT * FROM todo_section_table WHERE isTodoDeleted = 0 ORDER BY todoSectionNum ASC")
    fun getAllSections(): Flow<List<TodoSection>> // 削除されたものは表示しない

    @Query("DELETE FROM todo_section_table WHERE todoSectionNum = :sectionNum")
    suspend fun deleteSection(sectionNum: Int) // @Query で削除処理を記述

    @Query("UPDATE todo_section_table SET isTodoDeleted = 1 WHERE todoSectionNum = :sectionNum")
    suspend fun logicallyDeleteSection(sectionNum: Int)
}