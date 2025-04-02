package com.example.quicknotes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NotesModel::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun roomStoreDao(): RoomStoreDao

    companion object {
        private var DB_INSTANCE: AppDataBase? = null
        private const val BD_NAME = "NoteBook"
        fun getAppDb(context: Context): AppDataBase {
            if (DB_INSTANCE == null) {
                DB_INSTANCE = Room.databaseBuilder(context, AppDataBase::class.java, BD_NAME)
                    //.allowMainThreadQueries()
                    .build()
            }
            return DB_INSTANCE!!
        }
    }
}