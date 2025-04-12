package com.ynt.purrytify.ui.library

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ynt.purrytify.R
import com.ynt.purrytify.database.song.Song


class ListSongAdapter(var listSong: List<Song>, var likeSong: (Song) -> Unit) : RecyclerView.Adapter<ListSongAdapter.ListViewHolder>() {
//    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_song, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val song = listSong[position]
        holder.imgPhoto.load(song.image)
        holder.editButton.load(R.drawable.baseline_more_vert_24)
        holder.likeButton.load(R.drawable.twotone_check_24)
        if (song.isLiked==1) {
            holder.likeButton.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.green),
            )
        }
        else{
            holder.likeButton.setColorFilter(
                ContextCompat.getColor(holder.itemView.context,R.color.white)
            )
        }
        holder.tvTitle.text = song.title
        holder.tvArtist.text = song.artist
        holder.cardPlay.setOnClickListener({
            song.audio?.let { it1 -> playAudioFromUri(holder.itemView.context, it1.toUri()) }
        })
        holder.likeButton.setOnClickListener({
            likeSong(song)
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
        val diffCallback = SongDiffCallback(this.listSong, listSongs)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listSong = listSongs
        diffResult.dispatchUpdatesTo(this)
    }



    private fun playAudioFromUri(context: Context, uri: Uri) {
//        try {
//            mediaPlayer?.release()
//            mediaPlayer = MediaPlayer().apply {
//                setDataSource(context, uri)
//                prepare()
//                start()
//                setOnCompletionListener {
//                    it.release()
//                    mediaPlayer = null
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }


}