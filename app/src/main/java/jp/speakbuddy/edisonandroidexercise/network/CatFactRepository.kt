package jp.speakbuddy.edisonandroidexercise.network

import jp.speakbuddy.edisonandroidexercise.api.CatFactApiService
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.util.ErrorHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CatFactRepository @Inject constructor(
    private val apiService: CatFactApiService,
    private val errorHandler: ErrorHandler
) {

    /**
     * For the error handling exception(including no internet error screen), I decided not to show
     * a full blocking error screen. I wanted partial functionality regardless of an error that may
     * occur. I have added alternate functionality upon an error for the API below, if an error
     * were to occur, it would load the stored cat facts instead at random. If there were a critical
     * function like payment, then a more brash error screen should instil the importance of the
     * situation where the user should take immediate action, but since there is none like that in
     * this Kitty Fact application, there will be no implementation of it here.
     */
    suspend fun getACatFact(): CatFactResult<Fact> {
        return try {
            val catFact = apiService.getCatFact()
            CatFactResult.Success(catFact)
        } catch (e: Exception) {
            val errorMessage = errorHandler.handleException(e)
            CatFactResult.Error(errorMessage)
        }
    }

    /**
    Technically you can separate this Result into it's own class if it's being used widely in other
    areas but since it's not necessary in this case, i'm putting it here so it's easier to understand.
    The response can also be modified to a custom one if backend ever gives a custom response
    **/
    sealed class CatFactResult<out T> {
        data class Success<out T>(val data: T) : CatFactResult<T>()
        data class Error(val message: String?) : CatFactResult<Nothing>()
        data object Loading : CatFactResult<Nothing>()
    }
}