package com.kode.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailViewModel(private val uuid: String) : ViewModel() {
    val recipe = MutableLiveData<RecipeDetail>()

    init {
        GlobalScope.launch {
            recipe.postValue(API.instance.detailed(uuid))
        }
    }

    data class DetailViewModelFactory(val uuid: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DetailViewModel(uuid) as T
        }
    }
}
