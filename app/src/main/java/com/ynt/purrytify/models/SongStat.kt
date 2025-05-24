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