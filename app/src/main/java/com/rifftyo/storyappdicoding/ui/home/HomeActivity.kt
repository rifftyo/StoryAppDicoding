package com.rifftyo.storyappdicoding.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rifftyo.storyappdicoding.R
import com.rifftyo.storyappdicoding.adapter.StoryAdapter
import com.rifftyo.storyappdicoding.databinding.ActivityHomeBinding
import com.rifftyo.storyappdicoding.ui.MainActivity
import com.rifftyo.storyappdicoding.ui.ViewModelFactory
import com.rifftyo.storyappdicoding.ui.map.MapsActivity
import com.rifftyo.storyappdicoding.ui.upload.UploadActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var latitude: Double? = null
    private var longitude: Double? = null
    private val adapter = StoryAdapter()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            intent.putExtra(UploadActivity.LATITUDE, latitude)
            intent.putExtra(UploadActivity.LONGITUDE, longitude)
            startActivity(intent)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        checkAndRequestLocationPermission()
    }

    private fun setupRecyclerView() {
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            binding.tvError.isVisible = loadState.source.refresh is LoadState.Error

            if (loadState.source.refresh is LoadState.Error) {
                val errorState = loadState.source.refresh as LoadState.Error
                binding.tvError.text = errorState.error.localizedMessage
            }
        }

        viewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
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
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
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

    private fun checkAndRequestLocationPermission() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            getMyLastLocation()
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude

                    Toast.makeText(
                        this,
                        "Location: Lat: $latitude, Lon: $longitude",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.d("HomeActivity", "Location: Lat: $latitude, Lon: $longitude")
                } else {
                    Toast.makeText(this, "Unable to find location. Try again.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}