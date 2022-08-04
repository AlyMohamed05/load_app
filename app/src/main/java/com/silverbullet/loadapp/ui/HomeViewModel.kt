package com.silverbullet.loadapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silverbullet.loadapp.R
import com.silverbullet.loadapp.api.RetrofitApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

sealed class Result(val id: Int) {
    class Success(id: Int) : Result(id)
    class Failed(id: Int) : Result(id)
    object IDLE : Result(Int.MIN_VALUE)
}

class HomeViewModel : ViewModel() {

    private val _isDownloading: MutableLiveData<Boolean?> = MutableLiveData(null)
    val isDownloading: LiveData<Boolean?>
        get() = _isDownloading

    private val _event = MutableLiveData<Result>(Result.IDLE)
     val responseEvent: LiveData<Result>
        get() = _event

    init {
        Timber.d("Initialized")
    }

    fun download(selectedID: Int) {
        viewModelScope.launch {
            _isDownloading.value = true
            try {
                val response = when (selectedID) {
                    R.id.glide_download_button -> RetrofitApi.instance.getGlidePage()
                    R.id.loadapp_download_button -> RetrofitApi.instance.getStarterProjectPage()
                    R.id.retrofit_download_button -> RetrofitApi.instance.getRetrofitPage()
                    else -> null
                }
                if (response?.code() == 200) {
                    _event.value = Result.Success(selectedID)
                } else {
                    _event.value = Result.Failed(selectedID)
                }
            } catch (e: Exception) {
                _event.value = Result.Failed(selectedID)
            }
            _isDownloading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("ViewModel is destroyed")
    }
}