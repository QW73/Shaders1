package com.qw73.shaders.ui.together


import android.content.res.AssetManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.qw73.shaders.databinding.FragmentShadersTogetherBinding
import com.qw73.shaders.utils.BlendRenderer
import com.qw73.shaders.utils.BubblesRenderer
import com.qw73.shaders.utils.PurpleLiquidRenderer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShadersTogetherFragment : Fragment(), View.OnTouchListener {

    @Inject
    lateinit var assetManager: AssetManager

    private lateinit var viewBinding: FragmentShadersTogetherBinding
    private val viewModel: ShadersTogetherViewModel by activityViewModels()

    private lateinit var renderer: BlendRenderer
    private lateinit var bubblesRenderer: BubblesRenderer
    private lateinit var purpleLiquidRenderer: PurpleLiquidRenderer
    private lateinit var glView: GLSurfaceView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBinding = FragmentShadersTogetherBinding.inflate(inflater, container, false)
        viewBinding.viewModel = viewModel

        glView = GLSurfaceView(context)
        glView.setEGLContextClientVersion(2)
        bubblesRenderer= BubblesRenderer(assetManager)
        purpleLiquidRenderer = PurpleLiquidRenderer(assetManager)
        renderer = BlendRenderer(bubblesRenderer,purpleLiquidRenderer)
        glView.setRenderer(renderer)
        glView.setOnTouchListener(this)

        return glView
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }

    // Код для шейдеров, зависящих от позиции курсора
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        val x = event?.x ?: 0.0f
        val y = event?.y ?: 0.0f

        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Обновление координат мыши при касании или перемещении пальца
             //   renderer.updateMousePosition(x / glView.width.toFloat(),
            //        1.0f - y / glView.height.toFloat())
                glView.requestRender() // Запросить перерисовку GLSurfaceView
            }
        }
        return true
    }
}
