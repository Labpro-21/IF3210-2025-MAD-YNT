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

    @Query("SELECT COUNT(*) FROM song WHERE owner = :username")
    fun countSongsPerUser(username: String): LiveData<Int>

    @Query("SELECT COUNT(*) FROM song WHERE owner = :username AND is_liked = 1")
    fun countSongLiked(username: String): LiveData<Int>

    @Query("SELECT COUNT(*) FROM song WHERE owner = :username AND last_played != 0")
    fun playedSongCount(username: String): LiveData<Int>

    @Query("SELECT * FROM song WHERE owner = :username ORDER BY date_added DESC LIMIT 10")
    fun getNewSongs(username: String): LiveData<List<Song>>

    @Query("SELECT * FROM song WHERE owner = :username AND last_played != 0 ORDER BY last_played DESC LIMIT 10")
    fun getRecentlyPlayed(username: String): LiveData<List<Song>>
}
