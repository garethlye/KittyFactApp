package jp.speakbuddy.edisonandroidexercise.ui.listAndSearch

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.speakbuddy.edisonandroidexercise.base.BaseViewModel
import jp.speakbuddy.edisonandroidexercise.data.CatFactLocalRepo
import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal
import jp.speakbuddy.edisonandroidexercise.util.AppHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KittyListAndSearchViewModel @Inject constructor(
    appHelper: AppHelper,
    private val repository: CatFactLocalRepo,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : KittyListAndSearchViewModelInt, BaseViewModel(appHelper) {

    var mCurrentSearchQuery = MutableStateFlow("")
    var mCatErrorMessage = MutableStateFlow("")

    //slightly hacky, but this calls the search function if it's already the current query type
    val mForceSearchTrigger = MutableStateFlow(Unit)

    override val catErrorMessage: StateFlow<String> get() = mCatErrorMessage

    enum class QueryType {
        //All query states the screen can exist in
        ALL, FAVOURITES, SEARCH
    }

    var currentQueryType = MutableStateFlow(QueryType.ALL)

    @OptIn(ExperimentalCoroutinesApi::class)
    override var mCatFactsFromLocalDb: StateFlow<PagingData<CatFactLocal>> =
        combine(currentQueryType, mForceSearchTrigger, mCurrentSearchQuery)
        { queryType, _, searchQuery ->
            when (queryType) {
                QueryType.ALL -> repository.getAllFacts()
                QueryType.FAVOURITES -> repository.getFavouriteFacts()
                QueryType.SEARCH -> repository.searchFacts(searchQuery)
            }
        }
            .flatMapLatest { it }
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    override fun getAllStoredCatFacts() {
        currentQueryType.value = QueryType.ALL
    }

    override fun getAllFavouriteCatFacts() {
        currentQueryType.value = QueryType.FAVOURITES
    }

    /**
     * Functions below do have some business logic, personally I think it's alright in this case?
     * Maybe.
     */
    override fun searchCatFacts(query: String) {
        mCurrentSearchQuery.value = query

        //prevent search from being called & updated twice
        if (currentQueryType.value == QueryType.SEARCH) {
            mForceSearchTrigger.value = Unit
        } else {
            currentQueryType.value = QueryType.SEARCH
        }
    }

    override fun deleteAllCatFacts() {
        viewModelScope.launch(ioDispatcher) {
            when (val result = repository.deleteAllFacts()) {
                is CatFactLocalRepo.LocalCatFactResult.Error -> {
                    mCatErrorMessage.value = result.errorMessage
                }

                is CatFactLocalRepo.LocalCatFactResult.Success -> {
                    getAllStoredCatFacts()
                }
            }
        }
    }

    override fun toggleFavoriteStatus(catFact: CatFactLocal) {
        viewModelScope.launch(ioDispatcher) {
            val updatedFact = catFact.copy(isFavourite = !catFact.isFavourite)
            when (val result = repository.updateCatFact(updatedFact)) {
                is CatFactLocalRepo.LocalCatFactResult.Error -> {
                    mCatErrorMessage.value = result.errorMessage
                }

                is CatFactLocalRepo.LocalCatFactResult.Success -> {
                    updateSingleFactState(updatedFact)
                }
            }
        }
    }

    fun updateSingleFactState(updatedCatFact: CatFactLocal) {
        mCatFactsFromLocalDb.map { pagingData ->
            {
                pagingData.map { catFact ->
                    if (catFact.id == updatedCatFact.id) updatedCatFact else catFact
                }
            }
        }
    }
}