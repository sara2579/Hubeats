package com.hubeats.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hubeats.app.data.entity.MenuItem
import com.hubeats.app.data.repository.HubEatsRepository
import kotlinx.coroutines.launch

class ManageMenuViewModel(
    private val repository: HubEatsRepository
) : ViewModel() {

    private val _selectedItem = MutableLiveData<MenuItem?>()
    val selectedItem: LiveData<MenuItem?> = _selectedItem

    private val _saveResult = MutableLiveData<Boolean?>()
    val saveResult: LiveData<Boolean?> = _saveResult

    fun getMenu(restaurantId: Int): LiveData<List<MenuItem>> {
        return repository.getMenu(restaurantId)
    }

    fun loadMenuItem(id: Int) {
        viewModelScope.launch {
            _selectedItem.value = repository.getMenuItem(id)
        }
    }

    fun saveMenuItem(item: MenuItem, isNew: Boolean) {
        viewModelScope.launch {
            try {
                if (isNew) {
                    repository.addMenuItem(item)
                } else {
                    repository.updateMenuItem(item)
                }
                _saveResult.value = true
            } catch (e: Exception) {
                _saveResult.value = false
            }
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }

    fun addMenuItem(item: MenuItem) {
        viewModelScope.launch {
            repository.addMenuItem(item)
        }
    }

    fun updateMenuItem(item: MenuItem) {
        viewModelScope.launch {
            repository.updateMenuItem(item)
        }
    }

    fun deleteMenuItem(item: MenuItem) {
        viewModelScope.launch {
            repository.deleteMenuItem(item)
        }
    }
}