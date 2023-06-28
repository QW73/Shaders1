package com.qw73.shaders.ui.single

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SingleShaderViewModel @Inject constructor(
) : ViewModel() {

    val fpsLiveData = MutableLiveData<Int>()

    fun setFps(fps: Int) {
        fpsLiveData.postValue(fps)
    }

}