package com.qw73.shaders.utils

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.qw73.shaders.shaders.purpleLiquidShaderCode
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ShaderRenderer: GLSurfaceView.Renderer {

    private val vertexShaderCode = """
    attribute vec4 aPosition;
    attribute vec2 aTextureCoord;
    varying vec2 vTextureCoord;
    
    void main() {
        gl_Position = aPosition;
        vTextureCoord = aTextureCoord;
    }
"""
    // Код вершинного шейдера
    private val fragmentShaderCode = purpleLiquidShaderCode // Код фрагментного шейдера

    private var program: Int = 0

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        program = createProgram(vertexShaderCode, fragmentShaderCode)
        GLES20.glUseProgram(program)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    private fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)

        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        return program
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}
