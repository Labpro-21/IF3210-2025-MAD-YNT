package com.ynt.purrytify.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.models.SongStat

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(song: Song)
    @Update
    fun update(song: Song)
    @Delete
    fun delete(song: Song)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(songStat: SongStat)

    @Query("SELECT * FROM SongStat WHERE user = :user AND year = :year AND month = :month AND day = :day AND songId = :songId AND artists = :artists LIMIT 1")
    suspend fun getSongStat(user: String, year: Int, month: Int, day: Int, songId: String, artists: String): SongStat?

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

    @Query("SELECT * FROM song WHERE owner = :username ORDER BY date_added DESC")
    fun getAllSongsRaw(username: String): List<Song>
}
