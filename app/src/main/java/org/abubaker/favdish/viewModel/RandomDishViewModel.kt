package org.abubaker.favdish.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RandomDishViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Random Dish Fragment"
    }

    val text: LiveData<String> = _text

}