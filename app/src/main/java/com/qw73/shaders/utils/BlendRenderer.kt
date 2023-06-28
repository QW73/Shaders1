package com.qw73.shaders.utils

import android.content.res.AssetManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BlendRenderer(
    private val bubblesRenderer: BubblesRenderer,
    private val purpleLiquidRenderer: PurpleLiquidRenderer
) : GLSurfaceView.Renderer {

    private val vertexShaderCode = """
        attribute vec4 aPosition;
        varying vec2 vTextureCoord;
        void main() {
            gl_Position = aPosition;
            vTextureCoord = (aPosition.xy + 1.0) / 2.0;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        uniform sampler2D uTexture0;
        uniform sampler2D uTexture1;
        varying vec2 vTextureCoord;
        void main() {
            vec4 color0 = texture2D(uTexture0, vTextureCoord);
            vec4 color1 = texture2D(uTexture1, vTextureCoord);
            gl_FragColor = mix(color0, color1, color1.a);
        }
    """

    private val vertices = floatArrayOf(
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        -1.0f, 1.0f, 0.0f,
        1.0f, 1.0f, 0.0f
    )

    private var program: Int = 0
    private var positionHandle: Int = 0
    private var vertexBuffer: FloatBuffer? = null
    private var textureHandle0: Int = 0
    private var textureHandle1: Int = 0

    init {
        val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        vertexBuffer = buffer.asFloatBuffer()
        vertexBuffer?.put(vertices)
        vertexBuffer?.position(0)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        program = createProgram(vertexShaderCode, fragmentShaderCode)
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        textureHandle0 = GLES20.glGetUniformLocation(program, "uTexture0")
        textureHandle1 = GLES20.glGetUniformLocation(program, "uTexture1")

    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bubblesRenderer.bubblesTextureId)
        GLES20.glUniform1i(textureHandle0, 0)

        if (GLES20.glGetError() != GLES20.GL_NO_ERROR) {
            Log.e("BlendRenderer2", "Error binding texture for uTexture0")
            return
        }


          //  ....

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