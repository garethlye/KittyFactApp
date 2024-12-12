package jp.speakbuddy.edisonandroidexercise.util

import jp.speakbuddy.edisonandroidexercise.data.CatFactLocalRepo
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.network.CatFactRepository
import javax.inject.Inject

class CatsUseCase @Inject constructor(
    private val repository: CatFactLocalRepo,
    private val catsMapper: CatsMapper
) {

    /**
    Normally I would call this as a util class like 'FactUtil' but it's written in a use-case style,
    it's easier for me to understand it's function, but for this i'll call it as use-case
     **/

    enum class CatFactStatus(val message: String) {
        ERROR_LOCAL("Failed to fetch fact from local storage"),
        NO_CATS("No local facts found")
    }

    fun catsCheck(fact: CatFactRepository.CatFactResult<Fact>): Boolean {
        // Does "cats" exist in string, ignore case-sensitive to ensure all cats are included
        return when (fact) {
            is CatFactRepository.CatFactResult.Error -> false
            is CatFactRepository.CatFactResult.Loading -> false
            is CatFactRepository.CatFactResult.Success -> fact.data.fact.contains(
                "cats",
                ignoreCase = true
            )
        }
    }

    suspend fun getLatestCatFactLocal(): CatFactRepository.CatFactResult<Fact> {
        return try {
            when (val localResult = repository.getLatestCatFact()) {
                is CatFactLocalRepo.LocalCatFactResult.Error -> {
                    CatFactRepository.CatFactResult.Error(localResult.errorMessage)
                }

                is CatFactLocalRepo.LocalCatFactResult.Success -> {
                    if (localResult.data != null) {
                        CatFactRepository.CatFactResult.Success(
                            catsMapper.mapLocalToFact(
                                localResult.data
                            )
                        )
                    } else {
                        CatFactRepository.CatFactResult.Error(CatFactStatus.NO_CATS.message)
                    }
                }
            }
        } catch (e: Exception) {
            CatFactRepository.CatFactResult.Error(e.message ?: CatFactStatus.ERROR_LOCAL.message)
        }
    }

    suspend fun getTotalCatFactNumber(currentNumber: Int): Int {
        /**
         * ID is incremental regardless of deleted cat facts if the DB has already been initialized,
         * this function will return the latest ID a.k.a the number of latest incremental fact added.
         * I'm also ignoring any errors that may occur here, I am assuming the only error is an
         * empty DB, since this is not a critical function I will not handle the error and block
         * the app from functioning.
         */

        return try {
            when (val localResult = repository.getLatestCatFact()) {
                is CatFactLocalRepo.LocalCatFactResult.Error -> {
                    0
                }

                is CatFactLocalRepo.LocalCatFactResult.Success -> {
                    localResult.data?.id
                        ?: if (currentNumber > 0) {
                            currentNumber
                        } else {
                            0
                        }
                }
            }
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getRandomCatFactLocal(): CatFactRepository.CatFactResult<Fact> {
        return try {
            when (val localResult = repository.getRandomCatFact()) {
                is CatFactLocalRepo.LocalCatFactResult.Error -> {
                    //if there was an error, it should be caught in the repo side
                    CatFactRepository.CatFactResult.Error(localResult.errorMessage)
                }

                is CatFactLocalRepo.LocalCatFactResult.Success -> {
                    if (localResult.data != null) {
                        CatFactRepository.CatFactResult.Success(localResult.data.let {
                            catsMapper.mapLocalToFact(
                                it
                            )
                        })
                    } else {
                        //if somehow the local result is empty, the db should be empty
                        CatFactRepository.CatFactResult.Error(CatFactStatus.NO_CATS.message)
                    }
                }
            }
        } catch (e: Exception) {
            CatFactRepository.CatFactResult.Error(e.message ?: CatFactStatus.ERROR_LOCAL.message)
        }
    }
}