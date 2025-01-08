package com.prvavaja.grocerease

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.prvavaja.grocerease.databinding.ActivityCameraBinding
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineStart
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.buildJsonObject
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.io.encoding.ExperimentalEncodingApi


typealias LumaListener = (luma: Double) -> Unit
class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraExecutor: ExecutorService
    private var flashMode = ImageCapture.FLASH_MODE_OFF
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        // viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }
//        binding.flashButton.setOnClickListener { changeFlashOnClick() }
        binding.rotateButton.setOnClickListener { rotateCamera() }
        cameraExecutor = Executors.newSingleThreadExecutor()
        // Back Btn
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
    @OptIn(ExperimentalEncodingApi::class)
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.SIZE, "1000")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

                contentValues)
            .build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    output.savedUri?.let { uri ->
                        val inputStream = contentResolver.openInputStream(uri)
                        val originalBitmap = BitmapFactory.decodeStream(inputStream)
                        val scaledBitmap = scaleBitmap(originalBitmap, 1024)
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream)
                        val byteArray = byteArrayOutputStream.toByteArray()
                        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT )

                        // SEND POST REQUEST
                        val client = OkHttpClient()
                        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
                        val cardImgUrl = JsonPrimitive(base64String)
                        val shopName = JsonPrimitive("")
                        val barcodeNum = JsonPrimitive("")

                        val jsonBody = buildJsonObject {
                            put("cardImgUrl", cardImgUrl)
                            put("pi_double", shopName)
                            put("pi_string", barcodeNum)
                        }

                        val requestBody = Json{prettyPrint = true}.encodeToString(jsonBody).toRequestBody(jsonMediaType)

                        val dotenv = dotenv {
                            directory = "./assets"
                            filename = "env" // instead of '.env', use 'env'
                        }
                        val apiHost = dotenv.get("API_HOST")
                        val apiPort = dotenv.get("API_PORT")

                        val request = Request.Builder()
                            .url("http://$apiHost:$apiPort/api/card/save")
                            .post(requestBody)
                            .build()

                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                Log.e(TAG, "Server image saved failed: ${e.message}")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                if (response.isSuccessful) {
                                    response.body?.let {
                                        Log.e(TAG, "SUCCESS Server image saved: ${it.string()}")
                                    }
                                } else {
                                    Log.e(TAG, "ERROR Server image saved: ${response.code} : ${response.message}")
                                }
                            }
                        })

                        val msg = "Photo capture succeeded: ${output.savedUri}"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "data:image/jpeg;base64,$base64String")

                    }
                }
            }
        )
    }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // If the width or height is larger than the maxSize, scale it down
        if (width > maxSize || height > maxSize) {
            val ratio = Math.min(maxSize.toFloat() / width, maxSize.toFloat() / height)
            val newWidth = (width * ratio).toInt()
            val newHeight = (height * ratio).toInt()
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        }

        return bitmap // No scaling needed if the image is small enough
    }


    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return
        //viewBinding.videoCaptureButton.isEnabled = false
        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@CameraActivity,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
//                        viewBinding.videoCaptureButton.apply {
//                            text = getString(R.string.stop_capture)
//                            isEnabled = true
//                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
//                        viewBinding.videoCaptureButton.apply {
//                            text = getString(R.string.start_capture)
//                            isEnabled = true
//                        }
                    }
                }
            }
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)
            imageCapture = ImageCapture.Builder().setFlashMode(flashMode).build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer, videoCapture)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }
//    private fun changeFlashOnClick(){
//        if (flashMode == ImageCapture.FLASH_MODE_OFF) {
//            flashMode = ImageCapture.FLASH_MODE_ON
//            binding.flashButton.text = getString(R.string.flash_on)
//        } else if (flashMode == ImageCapture.FLASH_MODE_ON) {
//            flashMode = ImageCapture.FLASH_MODE_AUTO
//            binding.flashButton.text = getString(R.string.flash_auto)
//        } else {
//            flashMode = ImageCapture.FLASH_MODE_OFF
//            binding.flashButton.text = getString(R.string.flash_off)
//        }
//        imageCapture?.flashMode = flashMode
//    }
    private fun rotateCamera() {
        if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }
    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }
        override fun analyze(image: ImageProxy) {
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()
            listener(luma)
            image.close()
        }
    }
}