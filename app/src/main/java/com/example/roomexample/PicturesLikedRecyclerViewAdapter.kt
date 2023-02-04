package com.example.roomexample

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomexample.models.Picture
import java.io.ByteArrayInputStream


class PicturesLikedRecyclerViewAdapter : ListAdapter<Picture, PicturesLikedRecyclerViewAdapter.PictureHolder>(DiffCallback()) {

    class PictureHolder(view: View) : RecyclerView.ViewHolder(view)

    private lateinit var listener: RecyclerClickListener
    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.picture_row_liked, parent, false)
        val pictureHolder = PictureHolder(v)

        val pictureDelete = pictureHolder.itemView.findViewById<ImageView>(R.id.picture_delete)
        pictureDelete.setOnClickListener {
            listener.onItemRemoveClick(pictureHolder.adapterPosition)
        }

        val picture = pictureHolder.itemView.findViewById<CardView>(R.id.picture)
        picture.setOnClickListener {
            listener.onItemClick(it, pictureHolder.adapterPosition)
        }

        return pictureHolder
    }

    override fun onBindViewHolder(holder: PictureHolder, position: Int) {
        val currentItem = getItem(position)
        val picture = holder.itemView.findViewById<ImageView>(R.id.picture_text)

        val inputStream = ByteArrayInputStream(currentItem.blob)
        val resized = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream), 1000, 800, true)
        picture.setImageBitmap(resized)
    }

    class DiffCallback : DiffUtil.ItemCallback<Picture>() {
        override fun areItemsTheSame(oldItem: Picture, newItem: Picture) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Picture, newItem: Picture) =
            oldItem == newItem
    }
}