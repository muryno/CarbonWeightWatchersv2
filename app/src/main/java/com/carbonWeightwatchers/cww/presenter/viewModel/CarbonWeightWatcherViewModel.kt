package com.carbonWeightwatchers.cww.presenter.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.carbonWeightwatchers.cww.App
import com.carbonWeightwatchers.cww.data.model.CarbonWeightWatcherModelItem
import com.carbonWeightwatchers.cww.data.util.Resource
import com.carbonWeightwatchers.cww.domain.UseCase.GetWeightWatcherUseCase
import com.carbonWeightwatchers.cww.presenter.adapter.weightWatcherAdapter
import com.carbonWeightwatchers.cww.presenter.di.ToastHelper
import com.carbonWeightwatchers.cww.utils.NetworkAvailabilityCheckUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class CarbonWeightWatcherViewModel @Inject constructor(
    private val getWeightWatcherUseCase: GetWeightWatcherUseCase,
    private val toastHelper: ToastHelper,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    @Inject
    lateinit var adapter: weightWatcherAdapter

    //for search
     var carbonWeightWatcherModelItem: List<CarbonWeightWatcherModelItem> = arrayListOf()

    //initial loading
    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val loading: LiveData<Boolean> = _loading

    //failed
    val failure: MutableLiveData<Boolean> = MutableLiveData()

    //show empty state
    private val _emptyState: MutableLiveData<Boolean> = MutableLiveData()
    val emptyState: LiveData<Boolean> = _emptyState


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val _weightWatcherLiveData = MutableLiveData<List<CarbonWeightWatcherModelItem>?>()
    val carbonWeightWatcherLiveData: LiveData<List<CarbonWeightWatcherModelItem>?> = _weightWatcherLiveData

    fun onCreate() {
        _loading.value = false
        failure.value = false

        getWeightWatcher()
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                getWeightWatcherUseCase.getLiveWeightWatcher().collect {
                    if(it.isNotEmpty()) {
                        adapter.differ.submitList(it)
                        //   showEmptyView(it.isEmpty())
                    }

                }
            }
        }
    }

    fun getWeightWatcher() {
        if (NetworkAvailabilityCheckUtils.isNetworkAvailable(App.instance?.applicationContext)>0) {
            coroutineScope.launch {
                try {
                    getWeightWatcherUseCase.fetchWeightWatchersFromApi().collect {
                        when (it) {
                            is Resource.Loading -> {
                               // if it is empty, load else don't
                                _loading.postValue(carbonWeightWatcherModelItem.isEmpty())
                            }
                            is Resource.Success -> {
                                withContext(Dispatchers.Main) {
                                    if (it.data.isNotEmpty()) {
                                        carbonWeightWatcherModelItem = it.data
                                        //save to db
                                        getWeightWatcherUseCase.saveWeightWatcher(it.data)
                                        // _weightWatcherLiveData.value = it.data
                                        _loading.postValue(false)
                                        failure.postValue(false)

                                    }
                                }
                            }
                            is Resource.Error -> {
                                withContext(Dispatchers.Main) {
                                    _loading.postValue(false)
                                    failure.postValue(true)
                                    toastHelper.sendToast(it?.error?.message ?: "error occur")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main)
                    {
                        toastHelper.sendToast(e.message.toString())
                    }
                }
            }
        } else {

                toastHelper.sendToast("Internet is not available")
        }
    }


    private fun showEmptyView(empty: Boolean) {
        if (empty)
            _emptyState.postValue(true)
        else
            _emptyState.postValue(false)
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}





