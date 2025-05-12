package com.ynt.purrytify.utils.queue

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.ynt.purrytify.models.Song


class QueueNode(
    var song: Song,
    var next: QueueNode? = null,
    var previous: QueueNode? = null
)

class QueueManager {
    private val _first = mutableStateOf<QueueNode?>(null)
    val first: State<QueueNode?> = _first

    private val _current = mutableStateOf<QueueNode?>(null)
    val current: State<QueueNode?> = _current

    private val _last = mutableStateOf<QueueNode?>(null)
    val last: State<QueueNode?> = _last

    private val _size = mutableStateOf(0)
    val size: State<Int> = _size

    fun addToQueue(song: Song) {
        var node = _first.value
        while (node != null) {
            if (node.song.id == song.id) return
            node = node.next
        }
        val newNode = QueueNode(song)
        if (_first.value == null) {
            _first.value = newNode
            _last.value = newNode
            _current.value = newNode
        } else {
            newNode.next = _first.value
            _first.value?.previous = newNode
            _first.value = newNode
        }
        _size.value++
    }

    fun addMultipleToQueue(songs: List<Song>) {
        songs.reversed().forEach { addToQueue(it) }
    }

    fun updateSong(updatedSong: Song): Boolean {
        var current = _first.value
        var updated = false
        while (current != null) {
            if (current.song.id == updatedSong.id) {
                current.song = updatedSong
                updated = true
                if (_current.value?.song?.id == updatedSong.id) {
                    _current.value?.song = updatedSong
                }
            }
            current = current.next
        }

        return updated
    }

    fun removeSong(song: Song) {
        var current = _first.value
        while (current != null) {
            if (current.song.id == song.id) {
                current.previous?.next = current.next
                current.next?.previous = current.previous
                if (current == _first.value) {
                    _first.value = current.next
                }
                if (current == _last.value) {
                    _last.value = current.previous
                }
                if (current == _current.value) {
                    _current.value = current.next ?: current.previous
                }
                _size.value--
                return
            }
            current = current.next
        }
    }

    fun clearQueue() {
        _first.value = null
        _current.value = null
        _last.value = null
        _size.value = 0
    }

    fun skipToNext(): Song? {
        _current.value = _current.value?.next ?: _first.value
        return _current.value?.song
    }

    fun skipToPrevious(): Song? {
        _current.value = _current.value?.previous ?: _last.value
        return _current.value?.song
    }

    fun getCurrentSong(): Song? {
        return _current.value?.song
    }

    fun setCurrentSong(song: Song): Boolean {
        var current = _first.value

        while (current != null) {
            if (current.song.id == song.id) {
                _current.value = current
                return true
            }
            current = current.next
        }

        return false
    }

    fun getQueueAsList(): List<Song> {
        val result = mutableListOf<Song>()
        var current = _first.value

        while (current != null) {
            result.add(current.song)
            current = current.next
        }

        return result
    }

    fun containsSong(song: Song): Boolean {
        var current = _first.value

        while (current != null) {
            if (current.song.id == song.id) {
                return true
            }
            current = current.next
        }

        return false
    }

    fun moveToFront(song: Song) {
        removeSong(song)
        addToQueue(song)
    }
}
