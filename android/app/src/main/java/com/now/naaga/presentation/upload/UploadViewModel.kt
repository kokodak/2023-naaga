package com.now.naaga.presentation.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.Coordinate
import com.now.domain.repository.PlaceRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.PlaceThrowable
import com.now.naaga.data.throwable.DataThrowable.UniversalThrowable
import com.now.naaga.util.singleliveevent.MutableSingleLiveData
import com.now.naaga.util.singleliveevent.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
) : ViewModel() {
    private var file = FILE_EMPTY

    val name = MutableLiveData<String>()

    private val _successUpload = MutableSingleLiveData<UploadStatus>()
    val successUpload: SingleLiveData<UploadStatus> = _successUpload

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    private val _coordinate = MutableLiveData<Coordinate>()
    val coordinate: LiveData<Coordinate> = _coordinate

    fun setFile(file: File) {
        this.file = file
    }

    fun setCoordinate(coordinate: Coordinate) {
        _coordinate.value = coordinate
    }

    fun isFormValid(): Boolean {
        return (file != FILE_EMPTY) && (_coordinate.value != null) && (name.value != null)
    }

    fun postPlace() {
        _coordinate.value?.let { coordinate ->
            _successUpload.setValue(UploadStatus.PENDING)
            viewModelScope.launch {
                runCatching {
                    placeRepository.postPlace(
                        name = name.value.toString(),
                        description = "",
                        coordinate = coordinate,
                        file = file,
                    )
                }.onSuccess {
                    _successUpload.setValue(UploadStatus.SUCCESS)
                }.onFailure {
                    _successUpload.setValue(UploadStatus.FAIL)
                    setThrowable(it)
                }
            }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> { _throwable.value = DataThrowable.NetworkThrowable() }
            is UniversalThrowable -> _throwable.value = throwable
            is PlaceThrowable -> _throwable.value = throwable
        }
    }

    companion object {
        val FILE_EMPTY = File("")

        const val ERROR_STORE_PHOTO = 215
        const val ERROR_POST_BODY = 205
    }
}
