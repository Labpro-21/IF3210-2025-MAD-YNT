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
    val artists: String,
    val timeListened: Long = 0,
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

@Entity(primaryKeys = ["year","month","user"])
data class MaxStreak(
    val year: Int,
    val month: Int,
    val user: String,
    val songId: String,
    val title: String,
    val artists: String,
    val maxStreak: Int,
    val image: String
)