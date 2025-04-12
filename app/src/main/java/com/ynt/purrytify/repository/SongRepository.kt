package com.ynt.purrytify.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.ynt.purrytify.database.song.Song
import com.ynt.purrytify.database.song.SongDao
import com.ynt.purrytify.database.song.SongRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SongRepository(application: Application) {
    private val mSongsDao: SongDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = SongRoomDatabase.getDatabase(application)
        mSongsDao = db.songDao()
    }
    fun getAllSongs(username: String): LiveData<List<Song>> = mSongsDao.getAllSongs(username)

    fun insert(song: Song) {
        executorService.execute { mSongsDao.insert(song) }
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
        return mSongsDao.getRecentlyPlayed((username))
    }
}