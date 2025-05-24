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

    @Query("SELECT SUM(timeListened) AS timeListened, month, year FROM SongStat WHERE user = :user GROUP BY month, year")
    fun getMonthlyTimeListened(user: String): LiveData<List<TimeListened>>

    @Query("""
        WITH count_song AS (
            SELECT songId, month, year, COUNT(*) AS listenedSongCount 
            FROM SongStat 
            WHERE user = :user 
            GROUP BY songId, month, year
        ),
        max_count AS (
            SELECT month, year, MAX(listenedSongCount) AS max_listened_count
            FROM count_song
            GROUP BY month, year
        )
        SELECT cs.songId, cs.month, cs.year, cs.listenedSongCount
        FROM count_song AS cs
        JOIN max_count AS mc
            ON cs.year = mc.year
            AND cs.month = mc.month
            AND cs.listenedSongCount = mc.max_listened_count
    """)
    fun getMonthlySongCount(user: String): LiveData<List<TopSong>>

    @Query(
        """
        WITH artist_count AS (
            SELECT month, year, artists, COUNT(*) AS listenedArtistCount
            FROM SongStat
            WHERE user = :user
            GROUP BY artists, month, year
        ),
        max_artist AS (
            SELECT month, year, MAX(listenedArtistCount) AS max_listened_artist
            FROM artist_count
            GROUP BY month, year
        )
        SELECT ac.artists, ac.month, ac.year, ac.listenedArtistCount
        FROM artist_count AS ac
        JOIN max_artist AS ma
            ON ac.month = ma.month
            AND ac.year = ma.year
            AND ac.listenedArtistCount = ma.max_listened_artist
    """
    )
    fun getMonthlyArtistCount(user: String): LiveData<List<TopArtist>>

    @Query("SELECT * FROM song WHERE owner = :user AND artist = :artist LIMIT 1")
    fun getOneSongByArtist(user: String, artist: String): Song

    @Query("SELECT * FROM song WHERE owner = :user AND id = :songId")
    fun getOneSongById(user: String, songId: Int): Song

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
