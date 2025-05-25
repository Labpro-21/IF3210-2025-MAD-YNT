package com.ynt.purrytify.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["user", "year", "month", "day", "songId"],
    foreignKeys = [
        ForeignKey(
            entity = Song::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("songId")]
)
data class SongStat(
    val user: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val songId: String,
    val title: String,
    val image: String,
    val artists: String,
    val timeListened: Long = 0,
)

data class TenTopSong(
    val title: String,
    val image: String,
    val artists: String,
    val month: Int,
    val year: Int,
    val timeListened: Long
)
data class TopTenArtist(
    val artists: String,
    val totalTime: Long,
    val image: String
)

data class TimeListened(
    val timeListened: Long,
    val month: Int,
    val year: Int
)

data class TopSong(
    val songId: String,
    val month: Int,
    val year: Int,
    val timeListened: Long
)

data class TopArtist(
    val artists: String,
    val month: Int,
    val year: Int,
    val totalTime: Long
)

data class TimeListenedPerDay(
    val year: Int,
    val month: Int,
    val day: Int,
    val totalTimeListened: Long
)
