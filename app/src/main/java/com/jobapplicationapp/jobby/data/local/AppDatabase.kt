package com.jobapplicationapp.jobby.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jobapplicationapp.jobby.data.model.JobApplication
import com.jobapplicationapp.jobby.data.model.User

@Database(entities = [JobApplication::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jobApplicationDao(): JobApplicationDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var Instance : AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // This SQL command adds the new column to your existing table
                db.execSQL("ALTER TABLE JobApplication ADD COLUMN appliedDate TEXT")
            }
        }
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}