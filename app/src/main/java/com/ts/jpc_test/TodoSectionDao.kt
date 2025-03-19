package com.ts.jpc_test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoSectionDao {
    // **TodoSection 用のメソッドを追加**
    @Query("SELECT COUNT(*) FROM todoSection")
    suspend fun getSectionCount(): Int // セクション数を取得

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todoSection: TodoSection): Long

    @Query("SELECT * FROM todoSection WHERE isTodoDeleted = 0 ORDER BY todoSectionNum ASC")
    fun getAllSections(): Flow<List<TodoSection>> // 削除されたものは表示しない

    @Query("DELETE FROM todoSection WHERE todoSectionNum = :sectionNum")
    suspend fun deleteSection(sectionNum: Int) // @Query で削除処理を記述

    @Query("UPDATE todoSection SET isTodoDeleted = 1 WHERE todoSectionNum = :sectionNum")
    suspend fun deleteLogicallySection(sectionNum: Int)
}