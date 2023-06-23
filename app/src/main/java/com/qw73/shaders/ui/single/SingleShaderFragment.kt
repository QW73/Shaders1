package com.qw73.shaders.ui.single

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.qw73.shaders.databinding.FragmentSingleShaderBinding
import com.qw73.shaders.shaders.purpleLiquidShaderCode
import com.qw73.shaders.utils.CustomEGLConfigChooser

import com.qw73.shaders.utils.ShaderRenderer
import dagger.hilt.android.AndroidEntryPoint
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@AndroidEntryPoint
class SingleShaderFragment : Fragment() {

    private lateinit var viewBinding: FragmentSingleShaderBinding
    private lateinit var renderer: ShaderRenderer
    private lateinit var glView: GLSurfaceView
    private val viewModel: SingleShaderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSingleShaderBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel

        glView = GLSurfaceView(context)
        glView.setEGLContextClientVersion(2)
        glView.setEGLConfigChooser(CustomEGLConfigChooser())
        renderer = ShaderRenderer()
        glView.setRenderer(renderer)


        return viewBinding.root
    }

    companion object {
        fun instance(): SingleShaderFragment {
            return SingleShaderFragment()
        }
    }
}

