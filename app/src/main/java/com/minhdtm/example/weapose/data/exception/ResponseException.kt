package com.minhdtm.example.weapose.data.exception

import com.minhdtm.example.weapose.data.remote.response.ServerErrorResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException

class ResponseException(
    private val _message: String? = null,
    private val _retrofit: Retrofit? = null,
    private val _response: Response<*>? = null,
    private val _throwable: Throwable? = null,
    private val _kind: Kind,
) : Throwable(_message) {
    enum class Kind {
        HTTP, HTTP_WITH_OBJECT, NETWORK, PREFERENCE, UNEXPECTED,
    }

    private var _errorServer: ServerErrorResponse? = null

    fun getKind() = _kind

    fun getThrowable() = _throwable

    fun getResponse() = _response

    fun getRetrofit() = _retrofit

    /**
     * The data returned from the server in the response body
     */
    fun getErrorData(): ServerErrorResponse? = _errorServer

    fun deserializeServerError() {
        val responseBody = _response?.errorBody()
        if (responseBody != null) {
            try {
                _errorServer = responseBody.getErrorBodyAs(ServerErrorResponse::class.java)
                responseBody.close()
            } catch (e: IOException) {
                Timber.e("Server error deserialization $e")
            }
        }
    }

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.
     * @throws IOException if unable to convert the body to the specified `type`.
     */

    @Throws(IOException::class)
    fun <T> ResponseBody.getErrorBodyAs(type: Class<T>): T? {
        if (_retrofit == null) {
            return null
        }

        val converter: Converter<ResponseBody, T> = _retrofit.responseBodyConverter(type, arrayOfNulls<Annotation>(0))
        return converter.convert(this)
    }

    companion object {
        fun http(response: Response<*>?, retrofit: Retrofit?): ResponseException {
            val message = response?.code().toString() + " - " + response?.message().toString()
            return ResponseException(
                _message = message,
                _response = response,
                _kind = Kind.HTTP,
                _retrofit = retrofit,
            )
        }

        fun httpObject(response: Response<*>?, retrofit: Retrofit?): ResponseException {
            val message = response?.code().toString() + " - " + response?.message().toString()
            val error = ResponseException(
                _message = message,
                _retrofit = retrofit,
                _kind = Kind.HTTP_WITH_OBJECT,
                _response = response,
            )
            error.deserializeServerError()
            return error
        }

        fun network(exception: IOException): ResponseException = ResponseException(
            _message = exception.message,
            _throwable = exception,
            _kind = Kind.NETWORK,
        )

        fun preferences(exception: Throwable): ResponseException = ResponseException(
            _message = exception.message,
            _throwable = exception,
            _kind = Kind.PREFERENCE,
        )

        fun unexpected(throwable: Throwable): ResponseException = ResponseException(
            _message = throwable.message,
            _throwable = throwable,
            _kind = Kind.UNEXPECTED,
        )
    }
}
