package jp.speakbuddy.edisonandroidexercise.ui.fact

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.speakbuddy.edisonandroidexercise.base.BaseViewModel
import jp.speakbuddy.edisonandroidexercise.data.CatFactLocalRepo
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.network.CatFactRepository
import jp.speakbuddy.edisonandroidexercise.util.AppHelper
import jp.speakbuddy.edisonandroidexercise.util.CatsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CatFactViewModel @Inject constructor(
    appHelper: AppHelper,
    private val catsUseCase: CatsUseCase,
    private val catFactLocalRepo: CatFactLocalRepo,
    private val catFactRepository: CatFactRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO //declare here so unit test can modify
) : CatFactViewModelInt, BaseViewModel(appHelper) {

    private val mMultipleCatsFound = MutableStateFlow(false)
    private val mNumberOfCatFacts = MutableStateFlow(0)
    private val mCatFactState =
        MutableStateFlow<CatFactRepository.CatFactResult<Fact>>(CatFactRepository.CatFactResult.Loading)

    override val catFactState: StateFlow<CatFactRepository.CatFactResult<Fact>> get() = mCatFactState
    override val multipleCatsFound: StateFlow<Boolean> get() = mMultipleCatsFound
    override val numberOfCatFacts: StateFlow<Int> get() = mNumberOfCatFacts

    /**
     * Technically I can use the BaseVM's updateLoader() function here, but in MVVM the VM shouldn't
     * update the UI. So the "loading" flow is returned instead.
     */
    override fun getANewCatFact() {
        viewModelScope.launch(ioDispatcher) {
            mCatFactState.value = CatFactRepository.CatFactResult.Loading
            val result = catFactRepository.getACatFact()
            mCatFactState.value = result
            mMultipleCatsFound.value = catsUseCase.catsCheck(result)
            if (result is CatFactRepository.CatFactResult.Success) {
                saveCatFact(result)
            }
        }
    }

    override fun getARandomLocalCatFact() {
        viewModelScope.launch(ioDispatcher) {
            mCatFactState.value = CatFactRepository.CatFactResult.Loading
            val result = catsUseCase.getRandomCatFactLocal()
            mCatFactState.value = result
            mMultipleCatsFound.value = catsUseCase.catsCheck(result)
        }
    }

    override fun getLatestCatFact() {
        viewModelScope.launch {
            mCatFactState.value = CatFactRepository.CatFactResult.Loading
            val result = catsUseCase.getLatestCatFactLocal()
            mCatFactState.value = result
            mMultipleCatsFound.value = catsUseCase.catsCheck(result)
        }
    }

    override fun getLatestCatFactNumber() {
        viewModelScope.launch {
            mNumberOfCatFacts.value = catsUseCase.getTotalCatFactNumber(mNumberOfCatFacts.value)
        }
    }

    // Save the cat fact into the local kitty repository
    private fun saveCatFact(catFactResult: CatFactRepository.CatFactResult<Fact>) {
        viewModelScope.launch(ioDispatcher) {
            /**
             * I know this is business logic in VM, but since I separated my Result class for
             * separation of concerns for Result, I opted to adding it here rather than added more
             * lines in my composable. Not entirely sure if that's a great idea or not.
             */
            if (catFactResult is CatFactRepository.CatFactResult.Success) {
                when (val result = catFactLocalRepo.insertFact(catFactResult.data)) {
                    is CatFactLocalRepo.LocalCatFactResult.Error -> mCatFactState.value =
                        CatFactRepository.CatFactResult.Error(result.errorMessage)

                    is CatFactLocalRepo.LocalCatFactResult.Success -> {
                        mNumberOfCatFacts.value =
                            catsUseCase.getTotalCatFactNumber(mNumberOfCatFacts.value)
                        //do nothing as it's a background update of the number of cats text
                    }
                }
            }
        }
    }
}
