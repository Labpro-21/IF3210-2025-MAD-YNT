package com.ynt.purrytify.models

import java.time.Month
import java.time.Year

data class SoundCapsule (
    val topArtists: Song,
    val topSong: Song,
    val month: Month,
    val year: Year,
)