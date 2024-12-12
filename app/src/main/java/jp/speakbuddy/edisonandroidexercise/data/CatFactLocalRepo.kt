package jp.speakbuddy.edisonandroidexercise.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import jp.speakbuddy.edisonandroidexercise.data.dao.CatFactDao
import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.util.CatsMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class CatFactLocalRepo @Inject constructor(
    private val dao: CatFactDao,
    private val catMapper: CatsMapper
) {

    /**
     * Universal message, error should be logged to crashlytics or Logcat for tracking typically.
     * Rather than showing exception error message which may be confusing to the user, just showing
     * a universal error would be better UX.
     */
    enum class LocalRepoStatus(val message: String) {
        ERROR_LOCAL_REPO("Failed to speak to local storage")
    }

    suspend fun insertFact(catFact: Fact): LocalCatFactResult<Unit> {
        if (dao.getCatFactByHash(catMapper.generateHashFact(catFact.fact)) == null) {
            //add only if it doesn't exist in the db
            return try {
                dao.addACatFact(catMapper.mapFactToLocal(catFact))
                LocalCatFactResult.Success(Unit)
            } catch (e: Exception) {
                LocalCatFactResult.Error(LocalRepoStatus.ERROR_LOCAL_REPO.message)
            }
        }
        //if the cat fact exists in the db, just assume it's fine and don't show an error
        return LocalCatFactResult.Success(Unit)
    }

    fun getAllFacts(): Flow<PagingData<CatFactLocal>> {
        //pagination for scalability, but it's local so it's too fast for it to visually be seen :(
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                try {
                    dao.getAllCatFactsAsList()
                } catch (e: Exception) {
                    // Could also log errors, but I won't in this case
                    EmptyPagingSource()
                }
            }
        ).flow
    }

    fun getFavouriteFacts(): Flow<PagingData<CatFactLocal>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                try {
                    dao.getFavouriteCatFacts()
                } catch (e: Exception) {
                    // Could also log errors, but I won't in this case
                    EmptyPagingSource()
                }
            }
        ).flow
    }

    fun searchFacts(query: String): Flow<PagingData<CatFactLocal>> {
        if (query.isBlank()) {
            return flowOf(PagingData.empty())
        }
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                try {
                    dao.searchCatFacts("%$query%")
                } catch (e: Exception) {
                    // Could also log errors, but I won't in this case
                    EmptyPagingSource()
                }
            }
        ).flow
    }

    suspend fun deleteAllFacts(): LocalCatFactResult<Unit> {
        return try {
            dao.deleteAllCatFacts()
            LocalCatFactResult.Success(Unit)
        } catch (e: Exception) {
            LocalCatFactResult.Error(LocalRepoStatus.ERROR_LOCAL_REPO.message)
        }
    }

    suspend fun updateCatFact(updatedFact: CatFactLocal): LocalCatFactResult<Unit> {
        return try {
            dao.updateCatFact(updatedFact)
            LocalCatFactResult.Success(Unit)
        } catch (e: Exception) {
            LocalCatFactResult.Error(LocalRepoStatus.ERROR_LOCAL_REPO.message)
        }
    }

    suspend fun getLatestCatFact(): LocalCatFactResult<CatFactLocal?> {
        return try {
            LocalCatFactResult.Success(dao.getLatestCatFact())
        } catch (e: Exception) {
            LocalCatFactResult.Error(LocalRepoStatus.ERROR_LOCAL_REPO.message)
        }
    }

    suspend fun getRandomCatFact(): LocalCatFactResult<CatFactLocal?> {
        return try {
            LocalCatFactResult.Success(dao.getRandomCatFact())
        } catch (e: Exception) {
            LocalCatFactResult.Error(LocalRepoStatus.ERROR_LOCAL_REPO.message)
        }
    }

    sealed class LocalCatFactResult<out T> {
        data class Success<out T>(val data: T? = null) : LocalCatFactResult<T>()
        data class Error(val errorMessage: String) : LocalCatFactResult<Nothing>()
    }
}