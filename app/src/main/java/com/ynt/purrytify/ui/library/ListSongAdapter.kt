package com.ynt.purrytify.ui.library

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ynt.purrytify.R
import com.ynt.purrytify.database.song.Song


class ListSongAdapter(var listSong: List<Song>) : RecyclerView.Adapter<ListSongAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_song, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val song = listSong[position]
        holder.imgPhoto.load(song.image)
        holder.editButton.load(R.drawable.baseline_more_vert_24)
        holder.likeButton.load(R.drawable.twotone_check_24)
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
        val cardPlay: ConstraintLayout = itemView.findViewById(R.id.card_play_area)
        val editButton: ImageView = itemView.findViewById(R.id.card_edit_image)
        val likeButton: ImageView = itemView.findViewById(R.id.card_like_image)
    }

    fun setListSongs(listSongs: List<Song>) {
        Log.d("ListSongAdapter", "setListSongs called with ${listSongs.size} songs")
        val diffCallback = SongDiffCallback(this.listSong, listSongs)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listSong = listSongs
        diffResult.dispatchUpdatesTo(this)
    }

}