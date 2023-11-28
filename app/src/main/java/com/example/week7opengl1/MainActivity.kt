package com.example.week7opengl1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val glview = findViewById<OpenGLView>(R.id.glview)

        findViewById<Button>(R.id.plusZ).setOnClickListener{
            glview.camera.translate(0f, 0f, 1f)
        }
        findViewById<Button>(R.id.plusY).setOnClickListener{
            glview.camera.translate(0f, 1f, 0f)
        }
        findViewById<Button>(R.id.plusX).setOnClickListener{
            glview.camera.translate(1f, 0f, 0f)
        }
        findViewById<Button>(R.id.minusZ).setOnClickListener{
            glview.camera.translate(0f, 0f, -1f)
        }
        findViewById<Button>(R.id.minusY).setOnClickListener{
            glview.camera.translate(0f, -1f, 0f)
        }
        findViewById<Button>(R.id.minusX).setOnClickListener{
            glview.camera.translate(-1f, 0f, 0f)
        }
        findViewById<Button>(R.id.rotateclockwise).setOnClickListener{
            glview.camera.rotate(-10f)
        }
        findViewById<Button>(R.id.rotateanticlockwise).setOnClickListener{
            glview.camera.rotate(10f)
        }
        findViewById<Button>(R.id.forward).setOnClickListener{
        var rad = glview.camera.rotation * (Math.PI/180)
         var  sine =  Math.sin(rad).toFloat()
            var cose =  Math.cos(rad).toFloat()
            glview.camera.translate(-sine, 0f, -cose)
        }
        findViewById<Button>(R.id.backward).setOnClickListener{
            var rad = glview.camera.rotation * (Math.PI/180)
            var  sine =  Math.sin(rad).toFloat()
            var cose =  Math.cos(rad).toFloat()
            glview.camera.translate(sine, 0f, cose)
        }

    }
}