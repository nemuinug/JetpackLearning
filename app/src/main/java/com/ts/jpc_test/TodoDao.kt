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
    suspend fun insert(todo: Todo) // データを新規追加。すでに同じデータがあった場合は上書き

    @Update
    suspend fun update(todo: Todo) // 更新

    @Delete
    suspend fun delete(todo: Todo) // 特定の ID のタスクを削除

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<Todo>) //複数のアイテムをまとめて挿入

    @Query("SELECT * FROM todo ORDER BY id ASC")
    fun getAll(): Flow<List<Todo>> // すべてのタスクを取得

    @Query("SELECT * FROM todo WHERE tag = :tag")
    fun getTodosByTag(tag: String): Flow<List<Todo>> //指定されたタグを持つタスクを取得する。

    @Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY id ASC")
    fun getActiveTodos(): Flow<List<Todo>> // 削除フラグが立っていないデータのみ取得

    @Query("UPDATE todo SET isDeleted = 1 WHERE id = :todoId")
    suspend fun markAsDeleted(todoId: Int) // 指定したIDのタスクを論理削除
}
