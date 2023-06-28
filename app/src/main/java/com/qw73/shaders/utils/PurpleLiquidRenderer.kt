package com.qw73.shaders.utils

import android.content.res.AssetManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class PurpleLiquidRenderer(private val assetManager: AssetManager) : GLSurfaceView.Renderer {


    // Код вершинного шейдера
    private val vertexShaderCode = """
        attribute vec4 aPosition;
        void main() {
            gl_Position = aPosition;
        }
    """

    private val vertices = floatArrayOf(
        -1.0f, -1.0f, 0.0f,  // Левая нижняя вершина
        1.0f, -1.0f, 0.0f,   // Правая нижняя вершина
        -1.0f, 1.0f, 0.0f,   // Левая верхняя вершина
        1.0f, 1.0f, 0.0f     // Правая верхняя вершина
    )

    private var fragmentShaderCode: String? = null
    var program: Int = 0
    private var positionHandle: Int = 0
    private var vertexBuffer: FloatBuffer? = null

    // FBO
    var purpleLiquidFrameBufferId: Int = 0
    private var purpleLiquidFrameBufferTextureId: Int = 0
    var purpleLiquidTextureId: Int = 0

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    private var mouseX: Float = 0.0f
    private var mouseY: Float = 0.0f
    private var time: Float = 0.0f

    // Юниформ-переменные
    var timeLocation: Int = 0
    var resolutionLocation: Int = 0
    private var mouseLocation: Int = 0

    init {

        // Буфер для вершин
        val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
        buffer.order(ByteOrder.nativeOrder())
        vertexBuffer = buffer.asFloatBuffer()
        vertexBuffer?.put(vertices)
        vertexBuffer?.position(0)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        fragmentShaderCode = loadShaderCodeFromAssets("purple_liquid.glsl")
        program = createProgram(vertexShaderCode, fragmentShaderCode ?: "")
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)

        timeLocation = GLES20.glGetUniformLocation(program, "iTime")
        resolutionLocation = GLES20.glGetUniformLocation(program, "iResolution")
        mouseLocation = GLES20.glGetUniformLocation(program, "iMouse")
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {

        GLES20.glViewport(0, 0, width, height)
        setViewSize(width, height)
        createFBO(width, height)
    }

    private fun createFBO(width: Int, height: Int) {
        // Удаляем предыдущий FBO, если он был создан
        if (purpleLiquidFrameBufferId != 0) {
            val frameBuffers = IntArray(1)
            frameBuffers[0] = purpleLiquidFrameBufferId
            GLES20.glDeleteFramebuffers(1, frameBuffers, 0)
        }
        if (purpleLiquidFrameBufferTextureId != 0) {
            val textures = IntArray(1)
            textures[0] = purpleLiquidFrameBufferTextureId
            GLES20.glDeleteTextures(1, textures, 0)
        }

        val fboIds = IntArray(1)
        GLES20.glGenFramebuffers(1, fboIds, 0)
        purpleLiquidFrameBufferId = fboIds[0]
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, purpleLiquidFrameBufferId)

        val texIds = IntArray(1)
        GLES20.glGenTextures(1, texIds, 0)
        purpleLiquidFrameBufferTextureId = texIds[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, purpleLiquidFrameBufferTextureId)
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, width, height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null)

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, purpleLiquidFrameBufferTextureId, 0)

        when (val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)) {
            GLES20.GL_FRAMEBUFFER_COMPLETE -> Log.d("FBO", "Framebuffer created successfully.")
            GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> Log.e("FBO", "Framebuffer creation failed due to incomplete attachment.")
            GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS -> Log.e("FBO", "Framebuffer creation failed due to mismatched attachment dimensions.")
            GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> Log.e("FBO", "Framebuffer creation failed due to missing attachment.")
            GLES20.GL_FRAMEBUFFER_UNSUPPORTED -> Log.e("FBO", "Framebuffer creation failed due to unsupported configurations.")
            else -> Log.e("FBO", "Unexpected error while creating framebuffer: $status")
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    override fun onDrawFrame(unused: GL10) {
        // Привязываем буфер кадров (framebuffer)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, purpleLiquidFrameBufferId)

        // Очищаем экран
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Устанавливаем текущую программу для отрисовки
        GLES20.glUseProgram(program)

        // Привязываем текстуру
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, purpleLiquidTextureId)

        // Обновляем время и устанавливаем значения uniform-переменных
        updateTime()
        val resolution = floatArrayOf(viewWidth.toFloat(), viewHeight.toFloat())
        val mouse = floatArrayOf(mouseX, mouseY, 0.0f, 0.0f)

        GLES20.glUniform1f(timeLocation, time)
        GLES20.glUniform2fv(resolutionLocation, 1, resolution, 0)
        GLES20.glUniform4fv(mouseLocation, 1, mouse, 0)

        // Draw the vertices
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(positionHandle)

        // Unbind the framebuffer after the drawing is done
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)

        // Now bind the default framebuffer and draw the texture to the screen
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Bind the texture we just drew to
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, purpleLiquidFrameBufferTextureId)

        // Draw the vertices again
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(positionHandle)
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

    // Загрузка вершинного шейдера
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        return shader
    }

    // Загрузка файла шейдера
    private fun loadShaderCodeFromAssets(fileName: String): String? {
        try {
            val inputStream = assetManager.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
            reader.close()
            return stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    private fun setViewSize(width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
    }

    //Обновление юниформ-переменных
    private fun updateTime() {
        time = System.nanoTime() / 1_000_000_000.0f
    }

    fun updateMousePosition(x: Float, y: Float) {
        mouseX = x
        mouseY = y
        Log.e("PurpleLiquidRenderer", " $mouseX $mouseY")
    }
}