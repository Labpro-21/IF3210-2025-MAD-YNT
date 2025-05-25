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
import com.ynt.purrytify.models.TenTopSong
import com.ynt.purrytify.models.TimeListened
import com.ynt.purrytify.models.TimeListenedPerDay
import com.ynt.purrytify.models.TopArtist
import com.ynt.purrytify.models.TopSong
import com.ynt.purrytify.models.TopTenArtist

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
    suspend fun getMonthlySongCount(user: String): List<TopSong>

    @Query("""
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
    """)
    suspend fun getMonthlyArtistCount(user: String): List<TopArtist>

    @Query("SELECT * FROM song WHERE owner = :user AND artist IN (:artist)")
    suspend fun getOneSongByArtist(user: String, artist: List<String>): List<Song>

    @Query("SELECT * FROM song WHERE owner = :user AND id IN (:songId)")
    suspend fun getOneSongById(user: String, songId: List<Int>): List<Song>

    @Query("SELECT * FROM song WHERE owner = :username ORDER BY date_added DESC")
    fun getAllSongs(username: String): LiveData<List<Song>>

    @Query("SELECT COUNT(*) FROM song WHERE owner = :username")
    suspend fun countSongsPerUser(username: String): Int

    @Query("SELECT COUNT(*) FROM song WHERE owner = :username AND is_liked = 1")
    suspend fun countSongLiked(username: String): Int

    @Query("SELECT COUNT(*) FROM song WHERE owner = :username AND last_played != 0")
    suspend fun playedSongCount(username: String): Int

    @Query("SELECT * FROM song WHERE owner = :username ORDER BY date_added DESC LIMIT 10")
    fun getNewSongs(username: String): LiveData<List<Song>>

    @Query("SELECT * FROM song WHERE owner = :username AND last_played != 0 ORDER BY last_played DESC LIMIT 10")
    fun getRecentlyPlayed(username: String): LiveData<List<Song>>

    @Query("SELECT * FROM song WHERE owner = :username ORDER BY date_added DESC")
    fun getAllSongsRaw(username: String): List<Song>

    @Query("SELECT COUNT(*) FROM SongStat WHERE user = :user")
    suspend fun songStatCountForUser(user: String): Int

    @Query("""
        SELECT title, image, artists, month, year, timeListened
        FROM songstat
        WHERE user = :user AND month = :month AND year = :year
        ORDER BY timeListened DESC
        LIMIT 10
    """)
    suspend fun getTenTopSong(user: String, month: Int, year: Int) : List<TenTopSong>

    @Query("""
        SELECT a.artists, a.totalTime, s.image
        FROM (
            SELECT artists, SUM(timeListened) AS totalTime
            FROM songstat
            WHERE user = :user AND month = :month AND year = :year
            GROUP BY artists
        ) AS a
        JOIN songstat s ON s.artists = a.artists
        WHERE s.user = :user AND s.month = :month AND s.year = :year
        GROUP BY a.artists
        ORDER BY a.totalTime DESC
        LIMIT 10
    """)
    suspend fun getTenTopArtist(user: String, month: Int, year: Int) : List<TopTenArtist>

    @Query("SELECT COUNT(*) FROM songstat WHERE user = :user AND month = :month AND year = :year")
    suspend fun getAllSongPerMonth(user: String, month: Int, year: Int) : Int

    @Query("SELECT COUNT(artists) FROM songstat WHERE user = :user AND month = :month AND year = :year")
    suspend fun getAllArtistPerMonth(user: String, month: Int, year: Int) : Int

    @Query("""
        SELECT 
            year, month, day, SUM(timeListened) AS totalTimeListened
        FROM songstat
        WHERE user = :user AND year = :year AND month = :month
        GROUP BY year, month, day
        ORDER BY day
    """)
    suspend fun getTimeListenedPerDay(user: String, month: Int, year: Int): List<TimeListenedPerDay>

    @Query("""
        SELECT 
            SUM(timeListened)
        FROM SongStat
        WHERE user = :user AND year = :year AND month = :month
        GROUP BY year, month
    """)
    suspend fun getTotalTimeListenedInMonth(user: String, month: Int, year: Int): Long
}

