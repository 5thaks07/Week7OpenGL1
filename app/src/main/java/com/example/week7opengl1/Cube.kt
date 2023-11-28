package com.example.week7opengl1

import android.util.Log
import freemap.openglwrapper.GPUInterface
import freemap.openglwrapper.OpenGLUtils
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube(val x: Float, val y: Float, val z: Float) {
    val vertexBuf: FloatBuffer
    val indexBuf: ShortBuffer
    val vertexandColurBuf: FloatBuffer
    init {
        // Define your vertices and add them to vertexBuf
        vertexBuf = OpenGLUtils.makeFloatBuffer(
            floatArrayOf(
                x, y+0.5f, z,
                x+0.5f, y+0.5f, z,
                x+0.5f, y+0.5f, z+0.5f,
                x, y+0.5f, z+0.5f,
                x, y, z,
                x+0.5f, y, z,
                x+0.5f, y, z+0.5f,
                x, y, z+0.5f
        ))
        vertexandColurBuf = OpenGLUtils.makeFloatBuffer(
            floatArrayOf(
                x, y+0.5f, z,
                0f, 1f, 0f,
                x+0.5f, y+0.5f, z,
                1f, 1f, 0f,
                x+0.5f, y+0.5f, z+0.5f,
                0f, 1f, 1f,
                x, y+0.5f, z+0.5f,
                1f, 1f, 0f,
                x, y, z,
                1f, 0f, 0f,
                x+0.5f, y, z,
                0f, 1f, 0f,
                x+0.5f, y, z+0.5f,
                0f, 0f, 1f,
                x, y, z+0.5f,
                0f, 1f, 1f,
            ))

        // Define your indices and add them to indexBuf
        indexBuf = OpenGLUtils.makeShortBuffer(
            shortArrayOf(
                0, 1, 2, 2, 3, 0,
                4, 5, 6, 6, 7, 4,
                1, 5, 6, 6, 2, 1,
                0, 4, 7, 7, 3, 0,
                0, 1, 5, 5, 4, 0,
                3, 2, 6, 6, 7, 3))
    }
     fun render(gpu: GPUInterface , refAttrib: Int){
        gpu.drawIndexedBufferedData(vertexBuf, indexBuf, 0, refAttrib)
     }
    fun renderMulti(gpu: GPUInterface){
        val stride = 24 // because one record contains vertices (12 bytes) and colours (12 bytes)
        val attrVarRef= gpu.getAttribLocation("aVertex")
        val colourVarRef = gpu.getAttribLocation("aColour")
        Log.d("OpenGLBasic", "Shader error: ${attrVarRef}")
        Log.d("OpenGLBasic", "Shader error: ${colourVarRef}")
        gpu.specifyBufferedDataFormat(attrVarRef, vertexandColurBuf, stride, 0)
        gpu.specifyBufferedDataFormat(colourVarRef, vertexandColurBuf, stride, 3)
        gpu.drawElements(indexBuf)
    }
}