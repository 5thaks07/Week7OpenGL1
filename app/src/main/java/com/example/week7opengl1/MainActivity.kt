package com.example.week7opengl1

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    var cameraPermission = false
    var surfaceTexture: SurfaceTexture? = null
    lateinit var  glview: OpenGLView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glview = OpenGLView(this) {
            surfaceTexture = it
            requestPermissions()
        }


        //    val glview = findViewById<OpenGLView>(R.id.glview)
        /*
        findViewById<Button>(R.id.plusZ).setOnClickListener {
            glview.camera.translate(0f, 0f, 1f)
        }
        findViewById<Button>(R.id.plusY).setOnClickListener {
            glview.camera.translate(0f, 1f, 0f)
        }
        findViewById<Button>(R.id.plusX).setOnClickListener {
            glview.camera.translate(1f, 0f, 0f)
        }
        findViewById<Button>(R.id.minusZ).setOnClickListener {
            glview.camera.translate(0f, 0f, -1f)
        }
        findViewById<Button>(R.id.minusY).setOnClickListener {
            glview.camera.translate(0f, -1f, 0f)
        }
        findViewById<Button>(R.id.minusX).setOnClickListener {
            glview.camera.translate(-1f, 0f, 0f)
        }
        findViewById<Button>(R.id.rotateclockwise).setOnClickListener {
            glview.camera.rotate(-10f)
        }
        findViewById<Button>(R.id.rotateanticlockwise).setOnClickListener {
            glview.camera.rotate(10f)
        }
        findViewById<Button>(R.id.forward).setOnClickListener {
            var rad = glview.camera.rotation * (Math.PI / 180)
            var sine = Math.sin(rad).toFloat()
            var cose = Math.cos(rad).toFloat()
            glview.camera.translate(-sine, 0f, -cose)
        }
        findViewById<Button>(R.id.backward).setOnClickListener {
            var rad = glview.camera.rotation * (Math.PI / 180)
            var sine = Math.sin(rad).toFloat()
            var cose = Math.cos(rad).toFloat()
            glview.camera.translate(sine, 0f, cose)
        }

         */
        setContentView(glview)

    }

    private fun startCamera(): Boolean {
        if (cameraPermission) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {

                    val surfaceProvider: (SurfaceRequest) -> Unit = { request ->
                        val resolution = request.resolution
                        surfaceTexture?.apply {
                            setDefaultBufferSize(resolution.width, resolution.height)
                            val surface = Surface(this)
                            request.provideSurface(
                                surface,
                                ContextCompat.getMainExecutor(this@MainActivity.baseContext)
                            )
                            { }

                        }
                    }
                    it.setSurfaceProvider(surfaceProvider)

                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview)

                } catch (e: Exception) {
                    Log.e("OpenGL01Log", e.stackTraceToString())
                }
            }, ContextCompat.getMainExecutor(this))
            return true
        } else {
            return false
        }
    }

    fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        } else {
            cameraPermission = true
            startCamera()

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraPermission = true
            startCamera()


        } else {
            AlertDialog.Builder(this).setPositiveButton("OK", null)
                .setMessage("CAMERA permission denied").show()
        }
    }


}
