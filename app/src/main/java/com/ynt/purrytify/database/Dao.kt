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
import com.ynt.purrytify.models.TimeListened
import com.ynt.purrytify.models.TopArtist
import com.ynt.purrytify.models.TopSong

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

    @Query("SELECT SUM(timeListened) AS timeListened, month, year FROM SongStat WHERE user = :user GROUP BY month, year ORDER BY month, year DESC")
    fun getMonthlyTimeListened(user: String): LiveData<List<TimeListened>>

    @Query("""
        WITH max_listened AS (
            SELECT month, year, MAX(timeListened) AS timeListened
            FROM SongStat
            WHERE user = :user
            GROUP BY month, year
        )
        SELECT ss.songId, ss.month, ss.year, ss.timeListened
        FROM SongStat ss
        JOIN max_listened as ml
            ON ss.month = ml.month
            AND ss.year = ml.year
            AND ss.timeListened = ml.timeListened
        ORDER BY ss.month, ss.year DESC
    """)
    fun getMonthlySongCount(user: String): LiveData<List<TopSong>>

    @Query(
        """
        WITH artist_total AS (
            SELECT artists, month, year, SUM(timeListened) AS totalTime
            FROM SongStat
            WHERE user = :user
            GROUP BY artists, month, year
        ),
        max_total AS (
            SELECT month, year, MAX(totalTime) AS maxTime
            FROM artist_total
            GROUP BY month, year
        )
        SELECT at.artists, at.month, at.year, at.totalTime
        FROM artist_total at
        JOIN max_total mt
          ON at.month = mt.month AND at.year = mt.year AND at.totalTime = mt.maxTime
        ORDER BY at.year, at.month DESC;

    """
    )
    fun getMonthlyArtistCount(user: String): LiveData<List<TopArtist>>

    @Query("SELECT * FROM song WHERE owner = :user AND artist = :artist LIMIT 1")
    fun getOneSongByArtist(user: String, artist: List<String>): LiveData<List<Song>>

    @Query("SELECT * FROM song WHERE owner = :user AND id IN (:songId)")
    fun getOneSongById(user: String, songId: List<Int>): LiveData<List<Song>>

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

    @Query("""
        SELECT s.*
        FROM song AS s
        JOIN songstat AS ss
            ON s.id = ss.songId
        WHERE owner = :user AND month = (:month) AND year = (:year) AND timeListened != 0
        GROUP BY s.id, month, year
        ORDER BY MAX(SUM(timeListened)), month, year DESC
        LIMIT 10
    """)
    fun getTenTopSong(user: String, month: List<Int>, year: List<Int>) : LiveData<List<Song>>
}
