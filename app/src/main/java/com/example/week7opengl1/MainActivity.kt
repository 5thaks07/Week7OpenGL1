package com.example.week7opengl1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import freemap.openglwrapper.GLMatrix

class MainActivity : AppCompatActivity(), SensorEventListener {
    var cameraPermission = false
    var surfaceTexture: SurfaceTexture? = null
    lateinit var glview: OpenGLView
    var accel: Sensor? = null
    var magField: Sensor? = null

    // Arrays to hold the current acceleration and magnetic field sensor values
    var accelValues = FloatArray(3)
    var magFieldValues = FloatArray(3)
    var orientionMatrix = FloatArray(16)
    var remappedMatrix = FloatArray(16)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sMgr = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        accel = sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magField = sMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sMgr.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
        sMgr.registerListener(this, magField, SensorManager.SENSOR_DELAY_UI)

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

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Leave blank
    }

    override fun onSensorChanged(ev: SensorEvent) {

        // Test which sensor has been detected.
        if (ev.sensor == accel) {

            // Copy the current values into the acceleration array
            accelValues = ev.values.copyOf()

        } else if (ev.sensor == magField) {

            // TODO ... do the same for the magnetic field values (not shown)
            magFieldValues = ev.values.copyOf()

        }


        // TODO Calculate the matrix.
        SensorManager.getRotationMatrix(orientionMatrix, null, accelValues, magFieldValues)
        SensorManager.remapCoordinateSystem(orientionMatrix,SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, remappedMatrix)
        glview.orientationMatrix = GLMatrix(remappedMatrix)

    }


}
