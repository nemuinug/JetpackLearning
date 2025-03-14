import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ts.jpc_test.Todo
import com.ts.jpc_test.TodoDao

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // メインスレッドで実行（デバッグ用）
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

