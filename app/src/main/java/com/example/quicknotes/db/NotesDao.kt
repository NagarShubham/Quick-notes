package com.example.quicknotes.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Upsert
    suspend fun upsertDataModel(dataModel: NotesModel)

    @Query("SELECT * FROM QuickNote ORDER BY updatedAt ASC")
    fun getAllNotesList(): Flow<List<NotesModel>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(user: NotesModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateNote(user: NotesModel)

    @Delete
    fun deleteNote(user: NotesModel)


}