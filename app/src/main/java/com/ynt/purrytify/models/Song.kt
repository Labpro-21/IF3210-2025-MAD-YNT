package com.ynt.purrytify.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Song(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "owner")
    var owner: String? = null,

    @ColumnInfo(name = "audio")
    var audio: String? = null,

    @ColumnInfo(name = "image")
    var image: String? = null,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "artist")
    var artist: String? = null,

    @ColumnInfo(name = "is_liked")
    var isLiked: Int = 0,

    @ColumnInfo(name = "duration")
    var duration: Int = 0,

    @ColumnInfo(name = "last_played")
    var lastPlayed: Long = 0,

    @ColumnInfo(name = "date_added")
    var dateAdded: Long = System.currentTimeMillis()

) : Parcelable
