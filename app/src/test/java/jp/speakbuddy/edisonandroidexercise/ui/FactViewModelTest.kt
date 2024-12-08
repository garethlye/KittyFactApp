package jp.speakbuddy.edisonandroidexercise.ui

import jp.speakbuddy.edisonandroidexercise.ui.fact.FactViewModel
import org.junit.Test

class FactViewModelTest {

    private val viewModel = FactViewModel()

    @Test
    fun updateFact() {
        var loading = true
        val initialFact = "initial"
        var fact = initialFact

        fact = viewModel.updateFact { loading = false }

        assert(!loading)
        assert(fact != initialFact)
    }
}
