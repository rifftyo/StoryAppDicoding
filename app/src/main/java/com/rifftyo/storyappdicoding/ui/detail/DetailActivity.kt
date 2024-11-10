package com.rifftyo.storyappdicoding.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.rifftyo.storyappdicoding.R
import com.rifftyo.storyappdicoding.data.Result
import com.rifftyo.storyappdicoding.databinding.ActivityDetailBinding
import com.rifftyo.storyappdicoding.ui.ViewModelFactory
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val idStory = intent.getStringExtra(EXTRA_ID)

        if (idStory != null) {
            getDetailStory(idStory)
        } else {
            binding.tvError.text = getString(R.string.error)
            binding.tvError.visibility = View.VISIBLE
        }

        supportActionBar?.hide()
    }

    private fun getDetailStory(id: String) {
        lifecycleScope.launch {
            viewModel.getDetailStory(id).observe(this@DetailActivity) { result ->
                binding.progressBar.visibility = View.VISIBLE
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val detailStory = result.data.story
                        Glide.with(this@DetailActivity)
                            .load(detailStory?.photoUrl)
                            .into(binding.imgDetail)
                        binding.tvDetailTitle.text = detailStory?.name
                        binding.tvDetailDescription.text = detailStory?.description
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.tvError.text = result.error
                        binding.tvError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}