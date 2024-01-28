package com.example.m19_location.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.m19_location.R
import com.example.m19_location.data.App
import com.example.m19_location.databinding.FragmentMainBinding
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.random.Random

class MainFragment : Fragment() {

    private var imageCapture: ImageCapture? = null
    private lateinit var executor: Executor

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val name =
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                startCamera(this.requireContext())
            } else {
                Toast.makeText(
                    this.requireContext(),
                    "permission is not granted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    companion object {

        private const val NOTIFICATION_ID = 1000
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"

        fun newInstance() = MainFragment()

        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val photoDao = (activity?.applicationContext as App).db.photoDao()
                return MainViewModel(photoDao) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Log.d("registration token", it.result)
        }

        executor = ContextCompat.getMainExecutor(this.requireContext())
        binding.button.setOnClickListener {
            takePhoto(this.requireContext())
        }

        binding.buttonShow.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, PhotoResultFragment())
                ?.commit();
        }

        binding.buttonMap.setOnClickListener {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.container, StartMapFragment())
                ?.commit();
//            throw RuntimeException("Test Crash")
        }

        checkPermission(this.requireContext())


    }

    private fun takePhoto(c: Context) {
        val imageCapture = imageCapture ?: return

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val outputOptions = activity?.let {
            ImageCapture.OutputFileOptions.Builder(
                it.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
            ).build()
        }

        outputOptions?.let {
            imageCapture.takePicture(
                it,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                        createNotification()
                        viewModel.saveSinglePhoto(outputFileResults.savedUri.toString())

                    }

                    override fun onError(exception: ImageCaptureException) {

                        exception.printStackTrace()
                    }
                }
            )
        }
    }


    private fun startCamera(c: Context) {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(c)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.camera.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, executor)
    }

    private fun checkPermission(c: Context) {

        val isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(c, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (isAllGranted) {
            startCamera(c)
            Toast.makeText(c, "permission is granted", Toast.LENGTH_SHORT).show()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    fun createNotification() {

        val intent = Intent(requireContext(), MainActivity::class.java)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
        else
            PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val notification = NotificationCompat.Builder(requireContext(), App.Notification_Channel_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Very good")
            .setContentText("You took a nice photo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setTimeoutAfter(1000)
            .build()

        NotificationManagerCompat.from(requireContext()).notify(Random.nextInt(),notification)
//        NotificationManagerCompat.from(requireContext()).notify(NOTIFICATION_ID,notification)
    }

}