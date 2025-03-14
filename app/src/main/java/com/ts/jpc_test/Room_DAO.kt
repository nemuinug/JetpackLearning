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
interface Room_Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Room_Entities)// データを新規追加。すでに同じデータがあった場合は上書き

    @Update
    suspend fun update(todo: Room_Entities)

    @Delete
    suspend fun delete(todo: Room_Entities)// 特定の ID のタスクを削除

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAll(): Flow<List<Room_Entities>> // すべてのタスクを取得

    @Query("SELECT * FROM todo_table WHERE tag = :tag")
    fun getTodosByTag(tag: String): Flow<List<Room_Entities>> //指定されたタグを持つタスクを取得する。
}
