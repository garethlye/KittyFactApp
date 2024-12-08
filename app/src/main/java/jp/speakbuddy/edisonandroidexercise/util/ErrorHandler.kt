package jp.speakbuddy.edisonandroidexercise.util

import retrofit2.HttpException
import java.io.IOException

class ErrorHandler {

    /**
     * Error messages are hardcoded here as they are not to be modified or translated.
     *
     * I've designed the app to still be functional even with an error, so no retry button is
     * required.
     */

    fun handleException(e: Exception): String {
        return when (e) {
            is HttpException -> handleHttpException(e)
            is IOException -> "Network error, please try again."
            else -> "An unexpected error occurred."
        }
    }

    /**
    For the cat fact API, unless the internet fails the only error I see it throwing is 429,
    so I added 429 specifically here and the other common ones with basic messages.
    I also thought of using enums here or strings.xml rather than keeping open "" strings, but
    since this is error related and should not be used anywhere else, i left it as open strings.
     **/
    private fun handleHttpException(e: HttpException): String {
        return when (e.code()) {
            400 -> "Bad request."
            401 -> "Unauthorized access."
            404 -> "Resource not found."
            429 -> "You're clicking too fast!"
            500 -> "Server error, please try again later."
            else -> "Something went wrong, error code: ${e.code()}"
        }
    }
}