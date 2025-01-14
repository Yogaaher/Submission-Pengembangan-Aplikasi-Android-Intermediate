package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.dicoding.picodiploma.loginwithanimation.data.response.StoryItem

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbardetail
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Story"
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, android.R.color.white))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val story = StoryItem(
            id = intent.getStringExtra("id") ?: "",
            name = intent.getStringExtra("name") ?: "",
            description = intent.getStringExtra("description") ?: "",
            photoUrl = intent.getStringExtra("photo_url") ?: "",
            lat = intent.getDoubleExtra("lat", 0.0),
            lon = intent.getDoubleExtra("lon", 0.0),
            createdAt = intent.getStringExtra("createdAt") ?: ""
        )

        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
        binding.tvCreatedAt.text = "Created at: ${story.createdAt}"

        val imageRequest = ImageRequest.Builder(this)
            .data(story.photoUrl)
            .target(binding.ivDetailPhoto)
            .build()

        val imageLoader = ImageLoader(this)
        imageLoader.enqueue(imageRequest)
    }
}
