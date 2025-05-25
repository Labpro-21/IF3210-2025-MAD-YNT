package com.ynt.purrytify.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.models.SongStat
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
}