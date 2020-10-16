package com.shine56.nicebus.base

import androidx.lifecycle.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

open class MyViewModel(): ViewModel() {
    protected val refreshLiveData = MutableLiveData<Int>()

    open fun refreshData(){
        refreshLiveData.value = refreshLiveData.value
    }

    open fun refreshData(value: Int){
        refreshLiveData.value = value
    }

    fun <T>execute(block: () -> T): LiveData<T> {
        return liveData(viewModelScope.coroutineContext+ Dispatchers.IO) {
            emit(block())
        }
    }
    fun <T>asyncExecute(block: () -> T): Deferred<T> {
        return viewModelScope.async(Dispatchers.IO){
            block()
        }
    }

    open fun onSuccess(requestCode: Int){}
    open fun onFail(e: Exception){}

    override fun onCleared() {
        super.onCleared()
    }
}