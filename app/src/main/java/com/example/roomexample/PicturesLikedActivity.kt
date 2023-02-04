package com.example.roomexample

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.roomexample.API.ApiClient
import com.example.roomexample.models.Picture
import com.example.roomexample.models.PictureDatabase
import kotlinx.coroutines.launch

class PicturesLikedActivity : AppCompatActivity() {

    private lateinit var adapter: PicturesLikedRecyclerViewAdapter
    private lateinit var key: String
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val pictureDatabase by lazy { PictureDatabase.getDatabase(this).pictureDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        val searchBar = findViewById<SearchView>(R.id.search_bar)
        searchBar.isVisible = false
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["keyValue"]

        key = value.toString()

        setRecyclerView()
        getPictures()
        swipeRefreshLayout = findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            getPictures()
            Handler().postDelayed( {
                swipeRefreshLayout.isRefreshing = false
            }, 3000)
        }
    }

    private fun setRecyclerView() {
        val picturesRecyclerview = findViewById<RecyclerView>(R.id.pictures_recyclerview)
        picturesRecyclerview.layoutManager = GridLayoutManager(this, 3)
        picturesRecyclerview.setHasFixedSize(true)
        adapter = PicturesLikedRecyclerViewAdapter()
        adapter.setItemListener(object : RecyclerClickListener {

            // Tap the 'X' to delete the picture.
            override fun onItemRemoveClick(position: Int) {
                val picturesList = adapter.currentList.toMutableList()
                val pictureId = picturesList[position].id
                val pictureBlob = picturesList[position].blob
                val pictureAuthor = picturesList[position].author
                val pictureDescription = picturesList[position].description
                val pictureDate = picturesList[position].date
                val pictureInsta = picturesList[position].insta
                val removePicture = Picture(pictureId, pictureBlob, pictureAuthor, pictureDescription, pictureDate, pictureInsta)
                picturesList.removeAt(position)
                adapter.submitList(picturesList)
                lifecycleScope.launch {
                    pictureDatabase.deletePicture(removePicture)
                    ApiClient.apiService.dislikePictureById(key, pictureId).onSuccess {

                        Toast.makeText(applicationContext, "✔ Supprimée des favoris", Toast.LENGTH_SHORT).show()

                    }.onFailure {
                        Toast.makeText(applicationContext, "❌ Erreur réseau", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Tap the picture to edit.
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@PicturesLikedActivity, ShowPictureLikedActivity::class.java)
                val picturesList = adapter.currentList.toMutableList()
                intent.putExtra("picture_blob", picturesList[position].blob)
                intent.putExtra("picture_author", picturesList[position].author)
                intent.putExtra("picture_title", picturesList[position].description)
                intent.putExtra("picture_date", picturesList[position].date)
                intent.putExtra("picture_insta", picturesList[position].insta)
                startActivity(intent)
            }
        })
        picturesRecyclerview.adapter = adapter
    }

    private fun getPictures() {
        lifecycleScope.launch {
            pictureDatabase.getPictures()?.collect { picturesList ->
                if (picturesList.isNotEmpty()) {
                    adapter.submitList(picturesList.reversed())
                }
            }
        }
    }


    // The '+' menu button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.home_list -> {// Open Activity
                val intent = Intent(this, PicturesActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.search_list -> {
                val intent = Intent(this, PicturesSearchActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.favorites_list -> {
                val intent = Intent(this, PicturesLikedActivity::class.java)
                startActivity(intent)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_picture, menu)
        return true
    }
}