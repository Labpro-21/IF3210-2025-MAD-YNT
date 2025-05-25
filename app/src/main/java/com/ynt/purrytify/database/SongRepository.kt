package com.ynt.purrytify.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.ynt.purrytify.models.MaxStreak
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.models.SongStat
import com.ynt.purrytify.models.TimeListened
import com.ynt.purrytify.models.TopArtist
import com.ynt.purrytify.models.TopSong
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SongRepository(application: Application) {
    private val mSongsDao: Dao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = SongRoomDatabase.getDatabase(application)
        mSongsDao = db.songDao()
    }

    fun getAllSongs(username: String): LiveData<List<Song>> = mSongsDao.getAllSongs(username)

    fun insert(song: Song) {
        executorService.execute { mSongsDao.insert(song) }
    }

    fun insert(maxStreak: MaxStreak){
        executorService.execute { mSongsDao.insert(maxStreak) }
    }

    suspend fun insert(songStat: SongStat){
        mSongsDao.insert(songStat)
    }

    suspend fun getSongStat(user: String, year: Int, month: Int, day: Int, songId: String, artists: String): SongStat? {
        return mSongsDao.getSongStat(user, year, month, day, songId, artists)
    }

    fun delete(song: Song) {
        executorService.execute { mSongsDao.delete(song) }
    }

    fun update(song: Song) {
        executorService.execute { mSongsDao.update(song) }
    }

    fun countSongsPerUser(username: String): LiveData<Int> {
        return mSongsDao.countSongsPerUser(username)
    }

    fun countLikedSong(username: String): LiveData<Int> {
        return mSongsDao.countSongLiked(username)
    }

    fun playedSongCount(username: String): LiveData<Int> {
        return mSongsDao.playedSongCount(username)
    }

    fun getNewSongs(username: String): LiveData<List<Song>> {
        return mSongsDao.getNewSongs(username)
    }

    fun getRecentlyPLayed(username: String): LiveData<List<Song>> {
        return mSongsDao.getRecentlyPlayed(username)
    }

    fun getAllSongsRaw(username: String) : List<Song> {
        return mSongsDao.getAllSongsRaw(username)
    }

    suspend fun getMonthlyTimeListened(user: String): List<TimeListened> {
        return mSongsDao.getMonthlyTimeListened(user)
    }

    suspend fun getMonthlySongCount(user: String): List<TopSong> {
        return mSongsDao.getMonthlySongCount(user)
    }

    suspend fun getMonthlyArtistCount(user: String): List<TopArtist> {
        return mSongsDao.getMonthlyArtistCount(user)
    }

    suspend fun getOneSongByArtist(user: String, artist: List<String>): List<Song> {
        return mSongsDao.getOneSongByArtist(user, artist)
    }

    suspend fun getOneSongById(user: String, songId: List<Int>): List<Song> {
        return mSongsDao.getOneSongById(user, songId)
    }

    suspend fun songStatCountForUser(user: String): Int {
        return mSongsDao.songStatCountForUser(user)
    }

    suspend fun getMonthlyMaxStreaksForUser(user: String): List<MaxStreak> {
        return mSongsDao.getMonthlyMaxStreaksForUser(user)
    }

    fun getMaxStreak (user: String, year: Int, month: Int): MaxStreak?{
        return mSongsDao.getMaxStreak(user,year,month)
    }

}
