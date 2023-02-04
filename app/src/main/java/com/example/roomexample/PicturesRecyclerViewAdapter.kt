package com.example.roomexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomexample.models.PicturesItem
import com.squareup.picasso.Picasso


class PicturesRecyclerViewAdapter : ListAdapter<PicturesItem, PicturesRecyclerViewAdapter.PictureHolder>(DiffCallback()) {

    class PictureHolder(view: View) : RecyclerView.ViewHolder(view)

    private lateinit var listener: RecyclerClickListener
    fun setItemListener(listener: RecyclerClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.picture_row, parent, false)
        val pictureHolder = PictureHolder(v)

        val picture = pictureHolder.itemView.findViewById<CardView>(R.id.picture)
        picture.setOnClickListener {
            listener.onItemClick(it, pictureHolder.adapterPosition)
        }

        return pictureHolder
    }

    override fun onBindViewHolder(holder: PictureHolder, position: Int) {
        val currentItem = getItem(position)
        val picture = holder.itemView.findViewById<ImageView>(R.id.picture_text)
        Picasso.get().load(currentItem.urls?.regular).resize(1000, 800).centerCrop().into(picture)
    }

    class DiffCallback : DiffUtil.ItemCallback<PicturesItem>() {
        override fun areItemsTheSame(oldItem: PicturesItem, newItem: PicturesItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PicturesItem, newItem: PicturesItem) =
            oldItem == newItem
    }
}