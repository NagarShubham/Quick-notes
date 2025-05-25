package com.example.quicknotes.di

import android.app.ApplicationErrorReport
import android.content.Context
import androidx.room.Room
import com.example.quicknotes.db.NoteDao
import com.example.quicknotes.db.NotesDatabase
import com.example.quicknotes.db.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotesDatabase {
        return Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
            "notes_database",
        ).build()
    }

    @Provides
    fun provideNoteDao(database: NotesDatabase): NoteDao {
        return database.noteDao()
    }

    @Singleton
    @Provides
    fun provideRepo(noteDao: NoteDao): NotesRepository {
        return NotesRepository(noteDao)
    }
}