package com.imageloadingapp.presentation.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imageloadingapp.data.remote.base.ApiState
import com.imageloadingapp.data.repository.ImageLoadRepository
import com.imageloadingapp.domain.model.ImageModelItem
import com.imageloadingapp.utils.AppUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageVM @Inject constructor(
    private val imageRepo: ImageLoadRepository
) : ViewModel() {

    val imagesResponseMLD = MutableLiveData<ApiState<ArrayList<ImageModelItem>>>()

    fun fetchImages() = viewModelScope.launch {

        imageRepo.getAllImages().onStart {
            imagesResponseMLD.value = ApiState.loading()
        }.catch {
            imagesResponseMLD.value = ApiState.error(AppUtils.errorFormatter(it))
        }.collect {
            imagesResponseMLD.value = ApiState.success(it.data)
        }
    }


}