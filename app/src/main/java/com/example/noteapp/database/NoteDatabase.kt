package com.example.noteapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.noteapp.models.Note
import com.example.noteapp.utilities.DATABASE_NAME


@Database(entities = arrayOf(Note::class), version = 2, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNodeDao(): NoteDao

    companion object {

        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    DATABASE_NAME
                ).addMigrations(MIGRATION_1_2).build()

                INSTANCE = instance

                instance

            }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notes_table ADD COLUMN color INTEGER")
            }
        }
    }
}