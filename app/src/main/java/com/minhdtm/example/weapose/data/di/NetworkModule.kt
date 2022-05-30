package com.minhdtm.example.weapose.data.di

import com.minhdtm.example.weapose.BuildConfig
import com.minhdtm.example.weapose.data.remote.apiservice.CurrentWeatherApiService
import com.minhdtm.example.weapose.data.remote.apiservice.OneCallApiService
import com.minhdtm.example.weapose.data.remote.calladapter.FlowCallAdapterFactory
import com.minhdtm.example.weapose.data.remote.interceptor.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    fun provideOkHttpClient(
        headerInterceptor: HeaderInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .callTimeout(TIME_OUT, TimeUnit.MINUTES)
        .connectTimeout(TIME_OUT, TimeUnit.MINUTES)
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(headerInterceptor)
        .build()

    @Provides
    fun provideRetrofit(
        flowCallAdapterFactory: FlowCallAdapterFactory,
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(flowCallAdapterFactory)
        .build()

    @Provides
    fun provideOneCallApiService(retrofit: Retrofit): OneCallApiService =
        retrofit.create(OneCallApiService::class.java)

    @Provides
    fun provideCurrentWeatherApiService(retrofit: Retrofit): CurrentWeatherApiService =
        retrofit.create(CurrentWeatherApiService::class.java)

    companion object {
        private const val TIME_OUT = 1L
    }
}
