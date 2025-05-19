package com.ynt.purrytify.ui.screen.libraryscreen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ynt.purrytify.R
import com.ynt.purrytify.models.Song
import com.ynt.purrytify.ui.screen.libraryscreen.utils.SongDiffCallback


class ListSongAdapter(var listSong: List<Song>,
                      var likeSong: (Song) -> Unit,
                      var editSong: (Song) -> Unit,
                      var playSong: (Song) -> Unit
) : RecyclerView.Adapter<ListSongAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_song, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val song = listSong[position]
        holder.imgPhoto.load(song.image)
        holder.editButton.load(R.drawable.baseline_more_vert_24)
        holder.editButton.setColorFilter(
            ContextCompat.getColor(holder.itemView.context,R.color.white)
        )
        if (song.isLiked==1) {
            holder.likeButton.load(R.drawable.baseline_favorite_24)
            holder.likeButton.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.red),
            )
        }
        else{
            holder.likeButton.load(R.drawable.baseline_favorite_border_24)
            holder.likeButton.setColorFilter(
                ContextCompat.getColor(holder.itemView.context,R.color.white)
            )
        }
        holder.tvTitle.text = song.title
        holder.tvArtist.text = song.artist
        holder.likeButton.setOnClickListener {
            likeSong(song)
        }
        holder.editButton.setOnClickListener {
            editSong(song)
        }
        holder.cardPlay.setOnClickListener {
            playSong(song)
        }

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
        val diffCallback = SongDiffCallback(this.listSong, listSongs)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listSong = listSongs
        diffResult.dispatchUpdatesTo(this)
    }
}