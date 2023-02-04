package com.example.roomexample

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.roomexample.API.ApiClient
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class PicturesSearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var adapter: PicturesRecyclerViewAdapter
    private lateinit var key: String
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var currentQuery: String? = null
    var page: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["keyValue"]

        key = value.toString()
        setRecyclerView()
        val searchBar = findViewById<SearchView>(R.id.search_bar)
        searchBar.setOnQueryTextListener(this)
        swipeRefreshLayout = findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {
            if(!currentQuery.isNullOrEmpty()){
                getPictures()
            }
            Handler().postDelayed( {
                swipeRefreshLayout.isRefreshing = false
            }, 3000)
        }


    }

    private fun getPictures() {
        lifecycleScope.launch {
            ApiClient.apiService.getPicturesFromQuery(key, currentQuery).onSuccess {
                val pictureSearch = it

                val picturesList = pictureSearch?.results

                if (picturesList?.isEmpty() == false) {
                    adapter.submitList(picturesList)
                }
            }.onFailure {
                Toast.makeText(applicationContext, "❌ Erreur réseau", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToList(page: Int?) {
        lifecycleScope.launch {
            ApiClient.apiService.getPicturesFromQuery(key, currentQuery, page).onSuccess {
                val pictureSearch = it
                val picturesList = pictureSearch?.results
                if (picturesList != null) {
                    val tempList = adapter.currentList.toMutableList()
                    tempList.addAll(picturesList)
                    adapter.submitList(tempList)
                }
            }.onFailure {
                Toast.makeText(applicationContext, "❌ Erreur réseau", Toast.LENGTH_SHORT).show()
            }

        }
    }

    val showPictureResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val id = result.data?.getStringExtra("id")

                lifecycleScope.launch {
                    updatePicture(id)
                }


            }
        }

    private fun updatePicture(id: String?) {
        lifecycleScope.launch {

            adapter.currentList.first { it.id == id }.liked_by_user = true
            val currentLikes = adapter.currentList.first { it.id == id }.likes
            if (currentLikes != null) {
                adapter.currentList.first { it.id == id }.likes = currentLikes+1
            }
        }
    }

    private fun setRecyclerView() {
        val picturesRecyclerview = findViewById<RecyclerView>(R.id.pictures_recyclerview)
        val mLayoutManager = GridLayoutManager(this, 3)
        picturesRecyclerview.layoutManager = mLayoutManager
        picturesRecyclerview.setHasFixedSize(true)
        adapter = PicturesRecyclerViewAdapter()
        var loading = true
        var pastVisiblesItems: Int
        var visibleItemCount: Int
        var totalItemCount: Int
        picturesRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = mLayoutManager.getChildCount()
                    totalItemCount = mLayoutManager.getItemCount()
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            page++
                            addToList(page)
                            loading = true
                        }
                    }
                }
            }
        })
        adapter.setItemListener(object : RecyclerClickListener {

            // Tap the 'X' to delete the picture.
            override fun onItemRemoveClick(position: Int) {

            }

            // Tap the picture to edit.
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@PicturesSearchActivity, ShowPictureActivity::class.java)
                val picturesList = adapter.currentList.toMutableList()
                intent.putExtra("picture_id", picturesList[position].id)
                intent.putExtra("picture_author", picturesList[position].user?.name)
                val picture = view.findViewById<ImageView>(R.id.picture_text)
                val bitmapDrawable = picture.drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                val bArray = stream.toByteArray()
                intent.putExtra("picture_array", bArray)
                intent.putExtra("picture_title", picturesList[position].description)
                intent.putExtra("picture_date", picturesList[position].created_at)
                intent.putExtra("picture_insta", picturesList[position].user?.instagram_username)
                intent.putExtra("picture_likes", picturesList[position].likes)
                intent.putExtra("picture_state", picturesList[position].liked_by_user)
                showPictureResultLauncher.launch(intent)
            }
        })
        picturesRecyclerview.adapter = adapter
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        currentQuery = query
        getPictures()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }
}