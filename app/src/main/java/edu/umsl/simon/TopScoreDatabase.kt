package edu.umsl.simon

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE = "notes"

@Database(
    entities = [RoomTopScore::class],
    version = 1,
    exportSchema = false
)
abstract class TopScoreDatabase : RoomDatabase() {
    abstract fun TopScoreDao(): TopScoreDao

    companion object {
        @Volatile
        private var instance: TopScoreDatabase? = null

        fun getInstance(context: Context): TopScoreDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): TopScoreDatabase {
            return Room.databaseBuilder(context, TopScoreDatabase::class.java, DATABASE)
                .build()
        }
    }
}