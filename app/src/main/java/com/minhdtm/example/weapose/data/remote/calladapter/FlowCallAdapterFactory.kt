package com.minhdtm.example.weapose.data.remote.calladapter

import android.util.Log
import com.minhdtm.example.weapose.data.mapper.WeatherExceptionMapper
import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject

class FlowCallAdapterFactory @Inject constructor(
    private val mapper: WeatherExceptionMapper,
) : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        Timber.d("return type: $returnType")
        check(returnType is ParameterizedType) {
            "Flow return type must be parameterized as Flow<Foo> or Flow<out Foo>"
        }

        if (getRawType(returnType) != Flow::class.java) {
            return null
        }



        val responseType = getParameterUpperBound(0, returnType)
        return FlowCallAdapter<Any>(retrofit, mapper, responseType)
    }
}
