package com.ts.jpc_test

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todo_table WHERE tag = :tag")
    fun getTodosByTag(tag: String): Flow<List<Todo>>
}
