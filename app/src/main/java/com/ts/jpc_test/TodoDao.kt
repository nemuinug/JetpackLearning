package com.ts.jpc_test

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
// データアクセスオブジェクト（DAO）：Room データベースの操作を定義
@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)// データを新規追加。すでに同じデータがあった場合は上書き

    @Update
    suspend fun update(todo: Todo)// 更新

    @Delete
    suspend fun delete(todo: Todo)// 特定の ID のタスクを削除

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAll(): Flow<List<Todo>> // すべてのタスクを取得

    @Query("SELECT * FROM todo_table WHERE tag = :tag")
    fun getTodosByTag(tag: String): Flow<List<Todo>> //指定されたタグを持つタスクを取得する。

    // **TodoSection 用のメソッドを追加**
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(todoSection: TodoSection) // `TodoSection` を追加

    @Query("SELECT * FROM todo_section_table ORDER BY todoSectionNum ASC")
    fun getAllSections(): Flow<List<TodoSection>> // すべてのセクションを取得

    @Delete
    suspend fun deleteSection(todoSection: TodoSection) // 特定のセクションを削除
}
