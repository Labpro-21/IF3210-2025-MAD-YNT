package com.ynt.purrytify.database.song

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SongDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(song: Song)
    @Update
    fun update(song: Song)
    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM song WHERE owner = :username ORDER BY date_added DESC")
    fun getAllSongs(username: String): LiveData<List<Song>>
}