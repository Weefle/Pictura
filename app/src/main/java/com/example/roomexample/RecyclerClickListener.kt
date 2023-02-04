package com.example.roomexample

import android.view.View

interface RecyclerClickListener {
    fun onItemRemoveClick(position: Int)
    fun onItemClick(view: View, position: Int)
}