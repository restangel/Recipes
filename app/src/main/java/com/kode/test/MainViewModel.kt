package com.kode.test

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

class MainViewModel : ViewModel(), SearchView.OnQueryTextListener {

    val adapter = MutableLiveData<MainActivity.RecipeListAdapter>()
    val filterState = FilterStateLiveData(DataProcessor(DataProcessor.SortType.NAME_ACS))

    init {
        renew()
    }

    fun renew() {
        GlobalScope.launch {
            val list = API.instance.list()
            adapter.postValue(MainActivity.RecipeListAdapter(list, filterState))
        }
    }

    companion object {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel() as T
            }
        }
    }

    class FilterStateLiveData(default: DataProcessor) : LiveData<DataProcessor>(default) {
        override fun getValue(): DataProcessor {
            return super.getValue()
                ?: throw IllegalStateException("Null value in FilterStateLiveData")
        }

        fun changeSortType(sortType: DataProcessor.SortType?) {
            if (sortType != null && value.sortType != sortType)
                value = DataProcessor(sortType, value.query)
        }

        fun changeQuery(query: String) {
            if (!value.query.contentEquals(query))
                value = DataProcessor(value.sortType, query)
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        filterState.changeQuery(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        filterState.changeQuery(newText)
        return true
    }
}