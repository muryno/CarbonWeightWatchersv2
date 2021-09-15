package com.carbonWeightwatchers.cww.presenterTest

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.carbonWeightwatchers.cww.domain.UseCase.GetWeightWatcherUseCase
import com.carbonWeightwatchers.cww.presenter.di.ToastHelper
import com.carbonWeightwatchers.cww.presenter.viewModel.CarbonWeightWatcherViewModel
import com.carbonWeightwatchers.cww.utils.BaseUnitTest
import com.carbonWeightwatchers.cww.utils.getOrAwaitValueTest
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@HiltAndroidTest
@ExperimentalCoroutinesApi
class ViewModelTest : BaseUnitTest() {

    @Mock
    private lateinit var appContext: Application
    lateinit var carbonWeightWatcherViewModel: CarbonWeightWatcherViewModel


    @Before
    fun setUp() {
        appContext = Application()
        carbonWeightWatcherViewModel = CarbonWeightWatcherViewModel(  GetWeightWatcherUseCase(UseCaseRepositoryMock()),ToastHelper(),SavedStateHandle())
    }


}