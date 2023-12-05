package com.minhdtm.example.weapose.presentation.ui

import android.content.Context
import android.location.Address
import com.google.android.gms.maps.model.LatLng
import com.minhdtm.example.weapose.base.BaseTest
import com.minhdtm.example.weapose.domain.usecase.GetCurrentLocationUseCase
import com.minhdtm.example.weapose.domain.usecase.GetCurrentWeatherUseCase
import com.minhdtm.example.weapose.domain.usecase.GetHourWeatherUseCase
import com.minhdtm.example.weapose.domain.usecase.GetLocationFromTextUseCase
import com.minhdtm.example.weapose.presentation.model.CurrentWeatherMapper
import com.minhdtm.example.weapose.presentation.model.HourWeatherMapper
import com.minhdtm.example.weapose.presentation.model.HourWeatherViewData
import com.minhdtm.example.weapose.presentation.ui.home.CurrentWeatherViewModel
import com.minhdtm.example.weapose.utils.callPrivateFunction
import com.minhdtm.example.weapose.utils.factory.ModelDefault
import com.minhdtm.example.weapose.utils.factory.ViewDataDefault
import com.minhdtm.example.weapose.utils.getPrivateProperty
import com.minhdtm.example.weapose.utils.toFlow
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class CurrentWeatherViewModelTest : BaseTest() {
    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    lateinit var context: Context

    @MockK
    lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase

    @MockK
    lateinit var currentWeatherMapper: CurrentWeatherMapper

    @MockK
    lateinit var getCurrentLocationUseCase: GetCurrentLocationUseCase

    @MockK
    lateinit var getHourWeatherUseCase: GetHourWeatherUseCase

    @MockK
    lateinit var hourWeatherMapper: HourWeatherMapper

    @MockK
    lateinit var getLocationFromTextUseCase: GetLocationFromTextUseCase

    private lateinit var viewModel: CurrentWeatherViewModel

    @Before
    fun setup() {
        viewModel = spyk(
            CurrentWeatherViewModel(
                context,
                getCurrentWeatherUseCase,
                currentWeatherMapper,
                getCurrentLocationUseCase,
                getHourWeatherUseCase,
                hourWeatherMapper,
                getLocationFromTextUseCase,
            ),
            recordPrivateCalls = true,
        )
    }

    @Test
    fun `getCurrentLocation success`() = runTest(mainDispatcherRule.testDispatcher) {
        every { getCurrentLocationUseCase() } returns flow { emit(ModelDefault.latLng()) }

        viewModel.getCurrentLocation()

        verify(exactly = 1) {
            viewModel["showLoading"]()
            viewModel["getCurrentWeather"](ModelDefault.latLng())
        }
    }

    @Test
    fun `getWeatherByLocation success`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.getWeatherByLocation(ModelDefault.latLng())

        verify(exactly = 1) {
            viewModel["showLoading"]()
            viewModel["getCurrentWeather"](ModelDefault.latLng())
        }
    }

    @Test
    fun `getWeatherByAddressName success`() = runTest(mainDispatcherRule.testDispatcher) {
        val address = spyk(Address(Locale.ENGLISH))
        every { address.longitude } returns 0.0
        every { address.latitude } returns 0.0
        every { getLocationFromTextUseCase(any()) } returns address.toFlow()

        viewModel.getWeatherByAddressName("")

        verify(exactly = 1) {
            viewModel["showLoading"]()
            viewModel["getCurrentWeather"](LatLng(0.0, 0.0))
        }
    }

    @Test
    fun `getCurrentWeather success`() = runTest(mainDispatcherRule.testDispatcher) {
        every { getCurrentWeatherUseCase(any()) } returns ModelDefault.currentWeather().toFlow()
        every { getHourWeatherUseCase(any()) } returns ModelDefault.hourWeather().toFlow()
        every { currentWeatherMapper.mapToViewData(any()) } returns ViewDataDefault.currentWeather()
        every { hourWeatherMapper.mapToViewData(any()) } returns ViewDataDefault.hourWeather()

        viewModel.callPrivateFunction<Unit>("getCurrentWeather", ModelDefault.latLng())

        assertEquals(viewModel.state.value.currentWeather, ViewDataDefault.currentWeather())
        assertNotEquals(
            viewModel.state.value.listHourlyWeatherToday,
            emptyList<HourWeatherViewData>()
        )
    }

    @Test
    fun `getCurrentWeather success with current location is in Ha Noi`() =
        runTest(mainDispatcherRule.testDispatcher) {
            every { getCurrentWeatherUseCase(any()) } returns ModelDefault.currentWeather().toFlow()
            every { getHourWeatherUseCase(any()) } returns ModelDefault.hourWeather().toFlow()
            every { currentWeatherMapper.mapToViewData(any()) } returns ViewDataDefault.currentWeather()
            every { hourWeatherMapper.mapToViewData(any()) } returns ViewDataDefault.hourWeather()

            viewModel.callPrivateFunction<Unit>("getCurrentWeather", ModelDefault.latLngHaNoi())

            assertEquals(viewModel.state.value.currentWeather, ViewDataDefault.currentWeather())
            assertNotEquals(
                viewModel.state.value.listHourlyWeatherToday,
                emptyList<HourWeatherViewData>()
            )
            assertEquals(
                viewModel.getPrivateProperty<LatLng>("currentLocation"),
                ModelDefault.latLngHaNoi()
            )
        }
}
