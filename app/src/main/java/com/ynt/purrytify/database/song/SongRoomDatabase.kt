package com.ynt.purrytify.database.song

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Song::class], version = 1)
abstract class SongRoomDatabase : RoomDatabase(){
    abstract fun songDao(): SongDao
    companion object {
        @Volatile
        private var INSTANCE: SongRoomDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): SongRoomDatabase {
            if (INSTANCE == null) {
                synchronized(SongRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                    SongRoomDatabase::class.java, "song_database")
                        .build()
                }
            }
            return INSTANCE as SongRoomDatabase
        }
    }
}