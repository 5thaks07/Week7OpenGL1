package com.example.week7opengl1

import android.content.Context
import android.graphics.SurfaceTexture

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import freemap.openglwrapper.Camera
import freemap.openglwrapper.GLMatrix
import freemap.openglwrapper.GPUInterface
import freemap.openglwrapper.OpenGLUtils
import java.io.IOException
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLView(ctx: Context, val Callback: (SurfaceTexture) -> Unit) : GLSurfaceView(ctx), GLSurfaceView.Renderer  {
    init {
        setEGLContextClientVersion(2) // specify OpenGL ES 2.0
        setRenderer(this) // set the renderer for this GLSurfaceView
    }
    // gpu objects
    val gpu = GPUInterface("default shader")
    val gpu2 = GPUInterface("colour shader")
    // texture gpu
    val gpuTexture = GPUInterface("texture")
    // vertex buffers
    var fbuf : FloatBuffer? = null
    var fbuf2 : FloatBuffer? = null
    var fbuf3 : FloatBuffer? = null
    // index buffer
    var indicesfbuf : ShortBuffer? = null
    var indicesfbuf2 : ShortBuffer? = null
    // cubes class(buffer)
    var cube: Cube = Cube(3f, 0f, 0f)
    var cube2: Cube = Cube(-3f, 0f, 0f)
    // colours
    val blue = floatArrayOf(0f,0f,1f,1f)
    val green = floatArrayOf(0f,1f,0f,1f)
    val red = floatArrayOf(1f,0f,0f,1f)
    val yellow = floatArrayOf(1f,1f,0f,1f)

    // create a camera object
    val camera = Camera(0f, 0f, 0f)
    // Create a variable to hold view matrix
    var viewMatrix = GLMatrix()
    // Create a variable to hold projection matrix
    val projectionMatrix = GLMatrix()
    // orientation matrix
    var orientationMatrix = GLMatrix()
    // texture
    var cameraFeedSurfaceTexture : SurfaceTexture? = null



    // We initialise the rendering here
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the background colour (red=0, green=0, blue=0, alpha=1)
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        // Enable depth testing - will cause nearer 3D objects to automatically
        // be drawn over further objects
        GLES20.glClearDepthf(1.0f)

        // creating a texture
        val textureId = OpenGLUtils.genTexture()
        Log.d("OpenGLBasic", "Texture id = $textureId")
        if(textureId != 0){
            OpenGLUtils.bindTextureToTextureUnit(textureId, GLES20.GL_TEXTURE0, OpenGLUtils.GL_TEXTURE_EXTERNAL_OES)
            cameraFeedSurfaceTexture = SurfaceTexture(textureId)
            Callback(cameraFeedSurfaceTexture!!)
        }




        try {
            val success = gpu.loadShaders(context.assets, "vertex.glsl", "fragment.glsl")
            if (!success) {
                Log.d("OpenGLBasic", "Shader error: ${gpu.lastShaderError}")
            }
            val success2 = gpu2.loadShaders(context.assets, "vertexColour.glsl", "fragmentColour.glsl")
            if (!success2) {
                Log.d("OpenGLBasic", "Shader error: ${gpu2.lastShaderError}")
            }
            val success3 = gpuTexture.loadShaders(context.assets, "vertexTexture.glsl", "fragmentTexture.glsl")
            if (!success3) {
                Log.d("OpenGLBasic", "Shader error: ${gpu.lastShaderError}")
            }
            gpuTexture.select()
            val refTextureUnit = gpuTexture.getUniformLocation("uTexture")
            gpuTexture.setUniformInt(refTextureUnit, 0)


            fbuf = OpenGLUtils.makeFloatBuffer(
                floatArrayOf(
                    0f, 0f, -3f,
                    1f, 0f, -3f,
                    0.5f, 1f, -3f,
                      -0.5f, 0f, -6f,
                      0.5f, 0f, -6f,
                      0f, 1f, -6f,
                    )
            )
            fbuf2 = OpenGLUtils.makeFloatBuffer(
                floatArrayOf(
                    0f, 0f, -2f,
                    1f, 0f, -2f,
                    1f, 1f, -2f,
                    0f, 1f, -2f,
                    )
            )
            fbuf3 = OpenGLUtils.makeFloatBuffer(
                floatArrayOf(
                    -1f, 1f, 0f,
                    -1f, -1f, 0f,
                     1f, -1f, 0f,
                     1f, 1f, 0f,
                )
            )
            indicesfbuf = OpenGLUtils.makeShortBuffer( shortArrayOf(0,1,2,2,3,0))
            indicesfbuf2 = OpenGLUtils.makeShortBuffer( shortArrayOf(0,1,2,2,3,0))


        } catch (e: IOException) {
            Log.d("OpenGLBasic", e.stackTraceToString())
        }
    }
    //We draw our shapes here and current frames
    //Is called multiple times per second(fps)
    override fun onDrawFrame(gl: GL10?) {
        // clear any previous setting from previous frame
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        // Only run if buffer is not null
        if(fbuf != null && fbuf2 != null && fbuf3 != null && indicesfbuf != null && indicesfbuf2 != null) {
            // Update the surface texture with the latest frame from the camera
            cameraFeedSurfaceTexture?.updateTexImage()
            // camerabackground

            gpuTexture.select()
            val ref_aVertex2 = gpuTexture.getAttribLocation("aVertex")
            // rectangle
            gpuTexture.drawIndexedBufferedData(fbuf3!!, indicesfbuf2!!, 0, ref_aVertex2)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)

            viewMatrix.setAsIdentityMatrix()
            viewMatrix = orientationMatrix.clone()
            viewMatrix.correctSensorMatrix()
            viewMatrix.rotateAboutAxis(-camera.rotation , 'y')
            viewMatrix.translate(-camera.position.x, -camera.position.y, -camera.position.z)
            // Selects this shader
            gpu.select()
            val refUView = gpu.getUniformLocation("uView")
            val refUProj = gpu.getUniformLocation("uProj")

            gpu.sendMatrix(refUView, viewMatrix)
            gpu.sendMatrix(refUProj, projectionMatrix)
            val ref_aVertex = gpu.getAttribLocation("aVertex")
            val ref_uColour = gpu.getUniformLocation("uColour")



            gpu.specifyBufferedDataFormat(ref_aVertex, fbuf!!, 0)
            //first triangle
            gpu.setUniform4FloatArray(ref_uColour, blue)
            gpu.drawBufferedTriangles(0, 3)
            //second triangle
            gpu.setUniform4FloatArray(ref_uColour, green)
            gpu.drawBufferedTriangles(3,3)
            // square
            gpu.setUniform4FloatArray(ref_uColour, yellow)
            gpu.drawIndexedBufferedData(fbuf2!!, indicesfbuf!!, 0, ref_aVertex)
            // cube 1
             cube.render(gpu, ref_aVertex)

            gpu2.select()

            val refUProj2 = gpu2.getUniformLocation("uPerspMtx")
            val refUView2 = gpu2.getUniformLocation("uMvMtx")

            gpu2.sendMatrix(refUView2, viewMatrix)
            gpu2.sendMatrix(refUProj2, projectionMatrix)
            // cube2
            cube2.renderMulti(gpu2)


        }
    }

    // Used if the screen is resized eg- when mobile is rotated
    override fun onSurfaceChanged(gl: GL10?, w: Int, h: Int) {
        GLES20.glViewport(0, 0, w, h)
        val hfov = 60.0f
        val aspect: Float = w.toFloat()/h
        projectionMatrix.setProjectionMatrix(hfov, aspect, 0.001f, 100f)
    }
}