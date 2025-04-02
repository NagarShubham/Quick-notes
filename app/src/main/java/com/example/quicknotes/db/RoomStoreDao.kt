package com.example.quicknotes.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.StateFlow

@Dao
interface RoomStoreDao {

    @Upsert
    suspend fun upsertDataModel(dataModel: NotesModel)

    @Query("SELECT * FROM QuickNote ORDER BY updatedAt ASC")
    fun getAllNotesList(): StateFlow<List<NotesModel>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(user: NotesModel): Boolean

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateNote(user: NotesModel)

    @Delete
    fun deleteNote(user: NotesModel)


}