package com.ynt.purrytify.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.ynt.purrytify.models.MaxStreak
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.models.SongStat
import com.ynt.purrytify.models.TenTopSong
import com.ynt.purrytify.models.TimeListened
import com.ynt.purrytify.models.TimeListenedPerDay
import com.ynt.purrytify.models.TopArtist
import com.ynt.purrytify.models.TopSong
import com.ynt.purrytify.models.TopTenArtist
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

    suspend fun countSongsPerUser(username: String): Int {
        return mSongsDao.countSongsPerUser(username)
    }

    suspend fun countLikedSong(username: String): Int {
        return mSongsDao.countSongLiked(username)
    }

    suspend fun playedSongCount(username: String): Int {
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

    fun getMonthlyTimeListened(user: String): LiveData<List<TimeListened>> {
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

    suspend fun getMaxStreak (user: String, year: Int, month: Int): MaxStreak?{
        return mSongsDao.getMaxStreak(user,year,month)
    }


    suspend fun getTenTopSong(user: String, month: Int, year: Int) : List<TenTopSong> {
        return mSongsDao.getTenTopSong(user, month, year)
    }

    suspend fun getAllSongPerMonth(user: String, month: Int, year: Int) : Int {
        return mSongsDao.getAllSongPerMonth(user, month, year)
    }

    suspend fun getAllArtistPerMonth(user: String, month: Int, year: Int) : Int {
        return mSongsDao.getAllArtistPerMonth(user, month, year)
    }

    suspend fun getTenTopArtist(user: String, month: Int, year: Int) : List<TopTenArtist> {
        return mSongsDao.getTenTopArtist(user, month, year)
    }

    suspend fun getTimeListenedPerDay(user: String, month: Int, year: Int): List<TimeListenedPerDay> {
        return mSongsDao.getTimeListenedPerDay(user, month, year)
    }

    suspend fun getTotalTimeListenedInMonth(user: String, month: Int, year: Int): Long {
        return mSongsDao.getTotalTimeListenedInMonth(user, month, year)
    }

}
