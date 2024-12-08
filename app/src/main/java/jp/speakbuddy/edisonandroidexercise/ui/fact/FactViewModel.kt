package jp.speakbuddy.edisonandroidexercise.ui.fact

import androidx.lifecycle.ViewModel
import jp.speakbuddy.edisonandroidexercise.network.FactServiceProvider
import kotlinx.coroutines.runBlocking

class FactViewModel : ViewModel() {
    fun updateFact(completion: () -> Unit): String =
        runBlocking {
            try {
                FactServiceProvider.provide().getFact().fact
            } catch (e: Throwable) {
                "something went wrong. error = ${e.message}"
            }.also { completion() }
        }
}
