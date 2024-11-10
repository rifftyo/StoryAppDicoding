package com.rifftyo.storyappdicoding.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rifftyo.storyappdicoding.R
import com.rifftyo.storyappdicoding.data.Result
import com.rifftyo.storyappdicoding.databinding.ActivityHomeBinding
import com.rifftyo.storyappdicoding.ui.MainActivity
import com.rifftyo.storyappdicoding.ui.ViewModelFactory
import com.rifftyo.storyappdicoding.ui.upload.UploadActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var storyAdapter: StoryAdapter
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        observeStories()
        viewModel.getStories()

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter(emptyList())
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = storyAdapter
    }

    private fun observeStories() {
        viewModel.stories.observe(this) { result ->
            binding.progressBar.visibility = View.VISIBLE
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val stories = result.data.listStory
                    storyAdapter.updateListStory(stories)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvError.text = result.error
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showConfirmationDialog()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.logout_title))
            setMessage(getString(R.string.logout_message))
            setPositiveButton(getString(R.string.sure)) { dialog, _ ->
                viewModel.clearUserToken()
                val intentLogin = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intentLogin)
                finish()
                dialog.dismiss()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}