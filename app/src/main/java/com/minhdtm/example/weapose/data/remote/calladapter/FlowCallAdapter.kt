package com.minhdtm.example.weapose.data.remote.calladapter

import com.minhdtm.example.weapose.data.exception.ResponseException
import com.minhdtm.example.weapose.data.mapper.WeatherExceptionMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.*
import java.io.IOException
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FlowCallAdapter<T>(
    private val retrofit: Retrofit,
    private val mapper: WeatherExceptionMapper,
    private val responseType: Type,
) : CallAdapter<T, Flow<T>> {
    override fun responseType() = responseType

    override fun adapt(call: Call<T>): Flow<T> = flow {
        emit(suspendCancellableCoroutine { cancellableContinuation ->
            call.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    try {
                        cancellableContinuation.resume(response.body()!!)
                    } catch (e: Exception) {
                        cancellableContinuation.resumeWithException(mapper.mapperToWeatherException(asResponseException(e, response)))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    cancellableContinuation.resumeWithException(mapper.mapperToWeatherException(asResponseException(t)))
                }
            })

            cancellableContinuation.invokeOnCancellation { call.cancel() }
        })
    }

    private fun asResponseException(
        throwable: Throwable,
        res: Response<T>? = null,
    ): ResponseException {
        // We had non-200 http error
        if (throwable is HttpException) {
            val response = throwable.response()

            return when (throwable.code()) {
                // On out api 422's get metadata in the response. Adjust logic here based on your needs
                422 -> ResponseException.httpObject(response, retrofit)
                else -> ResponseException.http(response, retrofit)
            }
        }

        if (res != null) {
            return ResponseException.httpObject(res, retrofit)
        }

        // A network error happened
        if (throwable is IOException) {
            return ResponseException.network(throwable)
        }

        // We don't know what happened. We need to simply convert to an unknown error
        return ResponseException.unexpected(throwable)
    }
}
