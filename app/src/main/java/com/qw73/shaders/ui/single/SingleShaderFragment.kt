package com.qw73.shaders.ui.single


import android.content.res.AssetManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.qw73.shaders.databinding.FragmentSingleShaderBinding
import com.qw73.shaders.utils.BubblesRenderer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SingleShaderFragment : Fragment() {

    @Inject
    lateinit var assetManager: AssetManager

    private lateinit var viewBinding: FragmentSingleShaderBinding
    private val viewModel: SingleShaderViewModel by activityViewModels()

    private lateinit var renderer: BubblesRenderer
    private lateinit var glView: GLSurfaceView

    private var containerWidth: Int = 0
    private var containerHeight: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = FragmentSingleShaderBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel

        val glContainer = viewBinding.glContainer

        glView = GLSurfaceView(context)
        glView.setEGLContextClientVersion(2)
        renderer = BubblesRenderer(assetManager,viewModel)
        glView.setRenderer(renderer)
       // glView.setOnTouchListener(this)

        glContainer.addView(glView)


        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        glView.addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            containerWidth = right - left
            containerHeight = bottom - top
            viewBinding.tvShaderScreenSize.text = "$containerWidth х $containerHeight"
        }

        viewBinding.btnPlus.setOnClickListener {
            increaseContainerSize(50)
        }

        viewBinding.btnMinus.setOnClickListener {
            decreaseContainerSize(50)
        }

        viewModel.fpsLiveData.observe(viewLifecycleOwner) { fps ->
            viewBinding.tvFps.text = "$fps fps"
        }
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }

    private fun increaseContainerSize(sizeDelta: Int) {
        val newWidth = containerWidth + sizeDelta
        val newHeight = containerHeight + sizeDelta

        if (newWidth <= viewBinding.root.width && newHeight <= viewBinding.root.height) {
            val layoutParams = viewBinding.glContainer.layoutParams
            layoutParams.width = newWidth
            layoutParams.height = newHeight
            viewBinding.glContainer.layoutParams = layoutParams
        } else {
            Toast.makeText(context, "Невозможно увеличить размер контейнера", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun decreaseContainerSize(sizeDelta: Int) {
        val newWidth = containerWidth - sizeDelta
        val newHeight = containerHeight - sizeDelta

        if (newWidth > 0 && newHeight > 0) {
            val layoutParams = viewBinding.glContainer.layoutParams
            layoutParams.width = newWidth
            layoutParams.height = newHeight
            viewBinding.glContainer.layoutParams = layoutParams
        } else {
            Toast.makeText(context, "Невозможно уменьшить размер контейнера", Toast.LENGTH_SHORT)
                .show()
        }
    }

  /*  // Код для шейдеров, зависящих от позиции курсора
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        val x = event?.x ?: 0.0f
        val y = event?.y ?: 0.0f

        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Обновление координат мыши при касании или перемещении пальца
                renderer.updateMousePosition(x / glView.width.toFloat(),
                    1.0f - y / glView.height.toFloat())
                glView.requestRender() // Запросить перерисовку GLSurfaceView
            }
        }
        return true
    }*/
}
