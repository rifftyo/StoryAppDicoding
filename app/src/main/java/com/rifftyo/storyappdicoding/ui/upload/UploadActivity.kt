package com.rifftyo.storyappdicoding.ui.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.rifftyo.storyappdicoding.R
import com.rifftyo.storyappdicoding.data.Result
import com.rifftyo.storyappdicoding.databinding.ActivityUploadBinding
import com.rifftyo.storyappdicoding.ui.ViewModelFactory
import com.rifftyo.storyappdicoding.ui.home.HomeActivity
import com.rifftyo.storyappdicoding.utils.ImageHelper.getImageUri
import com.rifftyo.storyappdicoding.utils.ImageHelper.reduceFileImage
import com.rifftyo.storyappdicoding.utils.ImageHelper.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private val viewModel: UploadViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private var previousImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpObserves()
        setUpListener()

        binding.uploadButton.isEnabled = false

        supportActionBar?.hide()
    }

    private fun setUpObserves() {
        viewModel.imageUri.observe(this) { uri ->
            if (uri != null) {
                binding.previewImage.setImageURI(uri)
            }
            updateUploadButtonState()
        }
    }

    private fun setUpListener() {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
        binding.description.addTextChangedListener {
            updateUploadButtonState()
        }
    }

    private fun updateUploadButtonState() {
        val isDescriptionNotEmpty = binding.description.text.toString().isNotEmpty()
        val isImageSelected = viewModel.imageUri.value != null
        binding.uploadButton.isEnabled = isDescriptionNotEmpty && isImageSelected
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setImageUri(uri)
            previousImageUri = uri
        } else {
            Toast.makeText(this, getString(R.string.no_gallery), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val uri = getImageUri(this)
        viewModel.setImageUri(uri)
        launcherIntentCamera.launch(uri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.imageUri.value?.let { uri ->
                binding.previewImage.setImageURI(uri)
            }
        } else {
            viewModel.setImageUri(previousImageUri)
            if (previousImageUri == null) {
                binding.previewImage.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this,
                        R.drawable.upload_image
                    )
                )
            }
            Toast.makeText(this, getString(R.string.no_image), Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage() {
        viewModel.imageUri.value.let { uri ->
            if (uri != null) {
                val imageFile = uriToFile(uri, this).reduceFileImage()
                Log.d("Image File", "showImage: ${imageFile.path}")
                val description = binding.description.text.toString()
                binding.progressBar.visibility = View.VISIBLE

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                val lat = intent.getDoubleExtra(LATITUDE, 0.0)
                val lon = intent.getDoubleExtra(LONGITUDE, 0.0)

                if (binding.switchLocation.isChecked) {
                    viewModel.uploadStoryLocation(multipartBody, requestBody, lat.toFloat(), lon.toFloat()).observe(this) { result ->
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val intentBack =
                                    Intent(this@UploadActivity, HomeActivity::class.java).apply {
                                        flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                startActivity(intentBack)
                            }

                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    viewModel.uploadStory(multipartBody, requestBody).observe(this) { result ->
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val intentBack =
                                    Intent(this@UploadActivity, HomeActivity::class.java).apply {
                                        flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                startActivity(intentBack)
                            }

                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
    }
}