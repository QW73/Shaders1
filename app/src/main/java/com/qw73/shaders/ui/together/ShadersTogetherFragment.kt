package com.qw73.shaders.ui.together


import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.qw73.shaders.databinding.FragmentShadersTogetherBinding
import com.qw73.shaders.shaders.purpleLiquidShaderCode
import com.qw73.shaders.shaders.rainDropShaderCode
import com.qw73.shaders.ui.single.SingleShaderFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShadersTogetherFragment : Fragment() {

    private lateinit var viewBinding: FragmentShadersTogetherBinding
    private val viewModel: ShadersTogetherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = FragmentShadersTogetherBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel
        return viewBinding.root
    }



    companion object {
        fun instance(): ShadersTogetherFragment {
            return ShadersTogetherFragment()
        }
    }
}