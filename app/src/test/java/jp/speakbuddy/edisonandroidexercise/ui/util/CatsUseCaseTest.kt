package jp.speakbuddy.edisonandroidexercise.ui.util

import jp.speakbuddy.edisonandroidexercise.data.CatFactLocalRepo
import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.network.CatFactRepository
import jp.speakbuddy.edisonandroidexercise.util.CatsMapper
import jp.speakbuddy.edisonandroidexercise.util.CatsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class CatsUseCaseTest {
    // Mocks
    private lateinit var repository: CatFactLocalRepo
    private lateinit var catsMapper: CatsMapper
    private val testDispatcher = UnconfinedTestDispatcher()

    // Subject under test
    private lateinit var useCase: CatsUseCase

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        catsMapper = mock()
        useCase = CatsUseCase(repository, catsMapper)
    }

    @Test
    fun `catsCheck should return true if fact contains 'cats' ignoring case`() {
        val fact = CatFactRepository.CatFactResult.Success(Fact("Cats are awesome", 1))

        val result = useCase.catsCheck(fact)

        assertTrue(result)
    }

    @Test
    fun `catsCheck should return false for non-cat facts`() {
        val fact = CatFactRepository.CatFactResult.Success(Fact("Dogs are great", 1))

        val result = useCase.catsCheck(fact)

        assertFalse(result)
    }

    @Test
    fun `catsCheck should return false for Error and Loading states`() {
        val errorResult = CatFactRepository.CatFactResult.Error("Error")
        val loadingResult = CatFactRepository.CatFactResult.Loading

        assertFalse(useCase.catsCheck(errorResult))
        assertFalse(useCase.catsCheck(loadingResult))
    }

    @Test
    fun `getLatestCatFactLocal should return mapped fact on success`() = runTest {
        val localFact = CatFactLocal(
            id = 1,
            fact = "Cats are awesome",
            isFavourite = false,
            factHashKey = "123",
            length = 12
        )
        val mappedFact = Fact("Mapped fact", 1)

        // Mock behavior
        whenever(repository.getLatestCatFact()).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Success(localFact)
        )
        whenever(catsMapper.mapLocalToFact(localFact)).thenReturn(mappedFact)

        val result = useCase.getLatestCatFactLocal()

        assertTrue(result is CatFactRepository.CatFactResult.Success)
        assertEquals(mappedFact, (result as CatFactRepository.CatFactResult.Success).data)
    }

    @Test
    fun `getLatestCatFactLocal should return error when repository returns null`() = runTest {
        whenever(repository.getLatestCatFact()).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Success(null)
        )

        val result = useCase.getLatestCatFactLocal()

        assertTrue(result is CatFactRepository.CatFactResult.Error)
        assertEquals(CatsUseCase.CatFactStatus.NO_CATS.message, (result as CatFactRepository.CatFactResult.Error).message)
    }

    @Test
    fun `getLatestCatFact should return the ID from the local result`() = runTest {
        val localFact = CatFactLocal(
            id = 1,
            fact = "Cats are awesome",
            isFavourite = false,
            factHashKey = "123",
            length = 12
        )

        // Mock behavior
        whenever(repository.getLatestCatFact()).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Success(localFact)
        )

        val result = useCase.getTotalCatFactNumber(5)

        assertEquals(1, result)
    }

    @Test
    fun `getTotalCatFactNumber should return currentNumber if local result is null and currentNumber is greater than 0`() = runTest {
        whenever(repository.getLatestCatFact()).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Success(null)
        )

        val result = useCase.getTotalCatFactNumber(5)

        assertEquals(5, result)
    }

    @Test
    fun `getTotalCatFactNumber should return 0 for error or invalid scenarios`() = runTest {
        whenever(repository.getLatestCatFact()).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Error("Error")
        )

        val result = useCase.getTotalCatFactNumber(5)

        assertEquals(0, result)
    }

    @Test
    fun `getRandomCatFactLocal should return mapped fact on success`() = runTest {
        val localFact = CatFactLocal(
            id = 1,
            fact = "Cats are awesome",
            isFavourite = false,
            factHashKey = "123",
            length = 12
        )
        val mappedFact = Fact("Mapped random fact", 2)

        // Mock behavior
        whenever(repository.getRandomCatFact()).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Success(localFact)
        )
        whenever(catsMapper.mapLocalToFact(localFact)).thenReturn(mappedFact)

        val result = useCase.getRandomCatFactLocal()

        assertTrue(result is CatFactRepository.CatFactResult.Success)
        assertEquals(mappedFact, (result as CatFactRepository.CatFactResult.Success).data)
    }

    @Test
    fun `getRandomCatFactLocal should return error when repository returns null`() = runTest {
        whenever(repository.getRandomCatFact()).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Success(null)
        )

        val result = useCase.getRandomCatFactLocal()

        assertTrue(result is CatFactRepository.CatFactResult.Error)
        assertEquals(CatsUseCase.CatFactStatus.NO_CATS.message, (result as CatFactRepository.CatFactResult.Error).message)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
}