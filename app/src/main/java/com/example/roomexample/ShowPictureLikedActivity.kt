package com.example.roomexample

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.jsibbold.zoomage.ZoomageView
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("RestrictedApi")
class ShowPictureLikedActivity : AppCompatActivity() {

    private lateinit var addPictureBackground: RelativeLayout
    private lateinit var addPictureWindowBg: LinearLayout
    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        val saveButton = findViewById<Button>(R.id.save)
        val pictureLikesView = findViewById<TextView>(R.id.picture_likes)

        pictureLikesView.isVisible = false
        val addPictureButton = findViewById<Button>(R.id.add_picture_button)
        addPictureButton.isVisible = false
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["keyValue"]

        key = value.toString()

        addPictureBackground = findViewById(R.id.add_picture_background)
        addPictureWindowBg = findViewById(R.id.add_picture_window_bg)

        setActivityStyle()

        val pictureAuthor = intent.getStringExtra("picture_author")
        val pictureBlob = intent.getSerializableExtra("picture_blob") as ByteArray?
        val pictureTitle = intent.getStringExtra("picture_title")
        val pictureDate = intent.getStringExtra("picture_date")
        val pictureInsta = intent.getStringExtra("picture_insta")

        val picture = findViewById<ZoomageView>(R.id.add_picture_text)
        val pictureTitleView = findViewById<TextView>(R.id.add_picture_title)
        val pictureAuthorView = findViewById<AppCompatTextView>(R.id.picture_author)
        val pictureDateView = findViewById<TextView>(R.id.picture_date)

        pictureAuthorView.text = pictureAuthor
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val outputFormat = SimpleDateFormat("EEEE d MMMM yyyy")
        val inputDate = inputFormat.parse(pictureDate)
        val outputDate = outputFormat.format(inputDate)
        pictureDateView.text = outputDate
        pictureTitleView.text = if (!pictureTitle.isNullOrEmpty()) pictureTitle else "❌ Aucune description"
        val inputStream = ByteArrayInputStream(pictureBlob)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        picture.setImageBitmap(bitmap)

        saveButton.setOnClickListener {
            saveToStorage(bitmap, pictureAuthor)
            onBackPressed()

        }
        val instaButton = findViewById<Button>(R.id.instagram)
        if(pictureInsta == null){
            instaButton.isClickable = false
            instaButton.text = "❌ Pas d'Instagram"
        }else {
            instaButton.setOnClickListener {


                val uri: Uri = Uri.parse("http://instagram.com/_u/" + pictureInsta)
                val likeIng = Intent(Intent.ACTION_VIEW, uri)

                likeIng.setPackage("com.instagram.android")

                try {
                    startActivity(likeIng)
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/" + pictureInsta)
                        )
                    )
                }

                onBackPressed()

            }
        }
    }

    private fun saveToStorage(bitmap: Bitmap, name: String?) {
        val imageName = name+"_"+System.currentTimeMillis()+".jpg"
        var fos : OutputStream? = null
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {

                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }


                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        }
        else
        {
            val imagesDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

            val image = File(imagesDirectory, imageName)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)

            Toast.makeText(applicationContext, "✔ Image enregistrée" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun setActivityStyle() {
        // Make the background full screen, over status bar
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        this.window.statusBarColor = Color.TRANSPARENT
        val winParams = this.window.attributes
        winParams.flags =
            winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        this.window.attributes = winParams

        // Fade animation for the background of Popup Window
        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            addPictureBackground.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        addPictureWindowBg.alpha = 0f
        addPictureWindowBg.animate().alpha(1f).setDuration(500)
            .setInterpolator(DecelerateInterpolator()).start()

        // Close window when you tap on the dim background
        addPictureBackground.setOnClickListener { onBackPressed() }
        addPictureWindowBg.setOnClickListener { /* Prevent activity from closing when you tap on the popup's window background */ }
    }


    override fun onBackPressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            addPictureBackground.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        addPictureWindowBg.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }
}