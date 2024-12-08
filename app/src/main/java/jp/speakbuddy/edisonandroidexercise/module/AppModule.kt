package jp.speakbuddy.edisonandroidexercise.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.speakbuddy.edisonandroidexercise.api.CatFactApiService
import jp.speakbuddy.edisonandroidexercise.data.CatFactLocalRepo
import jp.speakbuddy.edisonandroidexercise.data.dao.CatFactDao
import jp.speakbuddy.edisonandroidexercise.network.CatFactRepository
import jp.speakbuddy.edisonandroidexercise.util.AppHelper
import jp.speakbuddy.edisonandroidexercise.util.CatBreedTensorClassifier
import jp.speakbuddy.edisonandroidexercise.util.CatsMapper
import jp.speakbuddy.edisonandroidexercise.util.CatsUseCase
import jp.speakbuddy.edisonandroidexercise.util.ErrorHandler
import jp.speakbuddy.edisonandroidexercise.util.GeneralUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAppHelper(): AppHelper {
        return AppHelper()
    }

    @Provides
    fun provideCatsUseCase(
        catFactLocalRepo: CatFactLocalRepo,
        catsMapper: CatsMapper
    ): CatsUseCase {
        return CatsUseCase(catFactLocalRepo, catsMapper)
    }

    @Provides
    fun provideCatFactRepository(
        apiService: CatFactApiService,
        errorHandler: ErrorHandler
    ): CatFactRepository {
        return CatFactRepository(apiService, errorHandler)
    }

    @Provides
    fun provideCatFactLocalRepo(catFactDao: CatFactDao, catsMapper: CatsMapper): CatFactLocalRepo {
        return CatFactLocalRepo(catFactDao, catsMapper)
    }

    @Provides
    fun provideCatsMapper(): CatsMapper {
        return CatsMapper()
    }

    @Provides
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandler()
    }

    @Provides
    fun provideCatBreedTensorClassifier(generalUtils: GeneralUtils): CatBreedTensorClassifier {
        return CatBreedTensorClassifier(generalUtils)
    }

    @Provides //For unit testing
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideGeneralUtils(): GeneralUtils {
        return GeneralUtils()
    }
}