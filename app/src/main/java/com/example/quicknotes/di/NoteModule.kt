package com.example.quicknotes.di

import android.content.Context
import androidx.room.Room
import com.example.quicknotes.db.AppDataBase
import com.example.quicknotes.db.RoomStoreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NoteModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDataBase {
        return Room.databaseBuilder(context, AppDataBase::class.java, "BD_NAME")
            //.allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun provideAppDao(appDataBase: AppDataBase): RoomStoreDao {
        return appDataBase.roomStoreDao()
    }
}