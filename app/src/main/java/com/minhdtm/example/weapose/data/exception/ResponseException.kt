package com.minhdtm.example.weapose.data.exception

import com.minhdtm.example.weapose.data.remote.response.ServerErrorResponse
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException

class ResponseException(
    override val message: String? = null,
    private val retrofit: Retrofit? = null,
    private val response: Response<*>? = null,
    private val throwable: Throwable? = null,
    private val kind: Kind? = null,
) : Throwable(message) {
    enum class Kind {
        HTTP, HTTP_WITH_OBJECT, NETWORK, PREFERENCE, UNEXPECTED,
    }

    private var _errorServer: ServerErrorResponse? = null

    fun getKind() = kind

    fun getThrowable() = throwable

    fun getResponse() = response

    fun getRetrofit() = retrofit

    /**
     * The data returned from the server in the response body
     */
    fun getErrorData(): ServerErrorResponse? = _errorServer

    fun deserializeServerError() {
        val responseBody = response?.errorBody()
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
        if (retrofit == null) {
            return null
        }

        val converter: Converter<ResponseBody, T> = retrofit.responseBodyConverter(type, arrayOfNulls<Annotation>(0))
        return converter.convert(this)
    }

    companion object {
        fun http(response: Response<*>?, retrofit: Retrofit?): ResponseException {
            val message = response?.code().toString() + " - " + response?.message().toString()
            return ResponseException(
                message = message,
                response = response,
                retrofit = retrofit,
                kind = Kind.HTTP,
            )
        }

        fun httpObject(response: Response<*>?, retrofit: Retrofit?): ResponseException {
            val message = response?.code().toString() + " - " + response?.message().toString()
            val error = ResponseException(
                message = message,
                retrofit = retrofit,
                kind = Kind.HTTP_WITH_OBJECT,
                response = response,
            )
            error.deserializeServerError()
            return error
        }

        fun network(exception: IOException): ResponseException = ResponseException(
            message = exception.message,
            throwable = exception,
            kind = Kind.NETWORK,
        )

        fun preferences(exception: Throwable): ResponseException = ResponseException(
            message = exception.message,
            throwable = exception,
            kind = Kind.PREFERENCE,
        )

        fun unexpected(throwable: Throwable): ResponseException = ResponseException(
            message = throwable.message,
            throwable = throwable,
            kind = Kind.UNEXPECTED,
        )
    }
}
