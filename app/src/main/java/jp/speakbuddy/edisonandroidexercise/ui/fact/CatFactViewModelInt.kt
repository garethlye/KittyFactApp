package jp.speakbuddy.edisonandroidexercise.ui.fact

import jp.speakbuddy.edisonandroidexercise.base.BaseViewModelInt
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.network.CatFactRepository
import kotlinx.coroutines.flow.StateFlow

interface CatFactViewModelInt: BaseViewModelInt {
    fun getANewCatFact()
    fun getARandomLocalCatFact()
    fun getLatestCatFact()
    fun getLatestCatFactNumber()
    val catFactState: StateFlow<CatFactRepository.CatFactResult<Fact>>
    val multipleCatsFound: StateFlow<Boolean>
    val numberOfCatFacts: StateFlow<Int>
}