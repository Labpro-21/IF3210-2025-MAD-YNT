package com.ynt.purrytify.ui.library

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ynt.purrytify.R
import com.ynt.purrytify.database.song.Song


class ListSongAdapter(val listSong: ArrayList<Song>) : RecyclerView.Adapter<ListSongAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_song, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val song = listSong[position]
        holder.imgPhoto.load(song.image)
        holder.tvTitle.text = song.title
        holder.tvArtist.text = song.artist
        holder.cardView.setOnClickListener({

        })

    }

    override fun getItemCount(): Int = listSong.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_song_name)
        val tvArtist: TextView = itemView.findViewById(R.id.tv_artist_name)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
    }

    fun setListSongs(listSongs: List<Song>) {
        Log.d("ListSongAdapter", "setListSongs called with ${listSongs.size} songs")
        val diffCallback = SongDiffCallback(this.listSong, listSongs)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listSong.clear()
        this.listSong.addAll(listSongs)
        diffResult.dispatchUpdatesTo(this)
    }

}