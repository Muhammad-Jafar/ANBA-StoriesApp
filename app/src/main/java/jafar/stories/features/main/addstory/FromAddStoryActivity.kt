package jafar.stories.features.main.addstory

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jafar.stories.R
import jafar.stories.data.model.AddStoryLocation
import jafar.stories.data.model.AddStoryRequest
import jafar.stories.data.repository.Result
import jafar.stories.databinding.ActivityFromAddStoryBinding
import jafar.stories.features.main.AddStoryViewModel
import jafar.stories.features.main.MainActivity
import jafar.stories.utils.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FromAddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFromAddStoryBinding
    private lateinit var loading: AlertDialog
    private lateinit var viewModel: ViewModel
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constanta.REQUEST_CODE_PERMISSION && !allPermissionGranted()) {
            Toast.makeText(this, resources.getString(R.string.access), Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }

    private fun allPermissionGranted() = Constanta.REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFromAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarForm)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.elevation = 0f
        loading = showAlertLoading(this)
        setupViewModel()

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this, Constanta.REQUIRED_PERMISSION, Constanta.REQUEST_CODE_PERMISSION
            )
        }

        binding.apply {
            pickLocationButton.setOnClickListener { pickLocation() }
            cameraButton.setOnClickListener { startTakePhoto() }
            galleryButton.setOnClickListener { startGallery() }
            binding.uploadButton.setOnClickListener { uploadImage() }
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getStoryInstance(this)
        val addStoryViewModel: AddStoryViewModel by viewModels { factory }
        viewModel = addStoryViewModel
    }

    private fun pickLocation() {
        resultLauncher.launch(Intent(this, PickLocationStoryActivity::class.java))
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK && it.data != null) {
                val data =
                    it.data?.getParcelableExtra<AddStoryLocation>(Constanta.EXTRA_COORDINATE) as AddStoryLocation
                binding.apply {
                    titleSetLocation.visibility = View.VISIBLE
                    contentSetLocation.visibility = View.VISIBLE
                    val lat = data.lat
                    val lon = data.lon
                    val address = parseAddressLocation(this@FromAddStoryActivity, lat!!, lon!!)
                    contentSetLocation.text = address

                    (viewModel as AddStoryViewModel).let { result ->
                        result.isLocationPicked.postValue(true)
                        result.latitude.postValue(lat)
                        result.longitude.postValue(lon)
                    }
                }
            }
        }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(this, "jafar.stories.app", it)
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
        launcherIntentCamera.launch(intent)
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, " Choose a picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg: Uri = result.data?.data as Uri
                val myFile = uriToFile(selectedImg, this)
                getFile = myFile
                binding.previewImageView.setImageURI(selectedImg)
            } else Toast.makeText(this, getString(R.string.choose_file), Toast.LENGTH_SHORT).show()
        }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceImage(getFile as File)
            val description = binding.descStory.text.toString().trim()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", file.name, requestImageFile)

            (viewModel as AddStoryViewModel).let {
                val lat = it.latitude.value
                val lon = it.longitude.value
                val request = AddStoryRequest(imageMultipart, description, lat, lon)

                it.doUpload(request).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> loading.show()
                        is Result.Error -> {
                            loading.dismiss()
                            if (result.error.isNotEmpty())
                                MaterialAlertDialogBuilder(this)
                                    .setTitle(resources.getString(R.string.title_failed_upload))
                                    .setMessage(result.error)
                                    .setPositiveButton(resources.getString(R.string.button_ok))
                                    { dialog, _ -> dialog.dismiss() }.show()
                        }
                        is Result.Success -> {
                            loading.dismiss()
                            MaterialAlertDialogBuilder(this)
                                .setTitle(resources.getString(R.string.title_success_upload))
                                .setMessage(resources.getString(R.string.message_success_upload))
                                .setPositiveButton(resources.getString(R.string.button_ok))
                                { dialog, _ ->
                                    dialog.dismiss()
                                    startActivity(Intent(this, MainActivity::class.java))
                                }.show()
                        }
                    }
                }
            }
        } else {
            loading.dismiss()
            Toast.makeText(this, R.string.input_file_first, Toast.LENGTH_SHORT).show()
        }
    }

    private fun reduceImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLenght: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLenght = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLenght > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
