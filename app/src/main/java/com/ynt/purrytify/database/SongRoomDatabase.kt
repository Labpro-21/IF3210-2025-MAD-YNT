package com.ynt.purrytify.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.models.SongStat

@Database(entities = [Song::class, SongStat::class], version = 2)
abstract class SongRoomDatabase : RoomDatabase(){
    abstract fun songDao(): Dao
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