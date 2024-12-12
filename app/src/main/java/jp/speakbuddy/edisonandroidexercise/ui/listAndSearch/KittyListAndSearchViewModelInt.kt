package jp.speakbuddy.edisonandroidexercise.ui.listAndSearch

import androidx.paging.PagingData
import jp.speakbuddy.edisonandroidexercise.base.BaseViewModelInt
import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal
import kotlinx.coroutines.flow.StateFlow

interface KittyListAndSearchViewModelInt: BaseViewModelInt {
    fun getAllStoredCatFacts()
    fun getAllFavouriteCatFacts()
    fun deleteAllCatFacts()
    fun searchCatFacts(query: String)
    fun toggleFavoriteStatus(catFact: CatFactLocal)
    var mCatFactsFromLocalDb: StateFlow<PagingData<CatFactLocal>>
    val catErrorMessage: StateFlow<String>
}