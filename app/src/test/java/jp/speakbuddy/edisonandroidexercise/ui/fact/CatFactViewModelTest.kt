package jp.speakbuddy.edisonandroidexercise.ui.fact

import app.cash.turbine.test
import jp.speakbuddy.edisonandroidexercise.data.CatFactLocalRepo
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.network.CatFactRepository
import jp.speakbuddy.edisonandroidexercise.util.AppHelper
import jp.speakbuddy.edisonandroidexercise.util.CatsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CatFactViewModelTest {

    private lateinit var viewModel: CatFactViewModel
    private val mockAppHelper: AppHelper = mock()
    private val mockCatsUseCase: CatsUseCase = mock()
    private val mockCatFactLocalRepo: CatFactLocalRepo = mock()
    private val mockCatFactRepository: CatFactRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        viewModel = CatFactViewModel(
            mockAppHelper,
            mockCatsUseCase,
            mockCatFactLocalRepo,
            mockCatFactRepository,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun `getANewCatFact should emit loading state and then success state`() = runTest {
        val fakeCatFact = Fact("Fake cat fact", 10, false)
        whenever(mockCatFactRepository.getACatFact()).thenReturn(
            CatFactRepository.CatFactResult.Success(
                fakeCatFact
            )
        )
        whenever(mockCatFactLocalRepo.insertFact(any())).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Success()
        )

        viewModel.getANewCatFact()
        advanceUntilIdle() //since i'm using Dispatcher.IO coroutines

        //assertEquals is using object reference rather than content, we compare the content instead
        viewModel.catFactState.test {
            assertEquals(CatFactRepository.CatFactResult.Success(fakeCatFact), awaitItem())
        }
    }

    @Test
    fun `getARandomLocalCatFact should emit loading state and then success state`() = runTest {
        val fakeCatFact = Fact("Fake local cat fact", 10, false)
        whenever(mockCatsUseCase.getRandomCatFactLocal()).thenReturn(
            CatFactRepository.CatFactResult.Success(
                fakeCatFact
            )
        )

        viewModel.getARandomLocalCatFact()
        advanceUntilIdle() //since i'm using Dispatcher.IO coroutines

        viewModel.catFactState.test {
            assertEquals(CatFactRepository.CatFactResult.Success(fakeCatFact), awaitItem())
        }
    }

    @Test
    fun `loadLatestCatFact should emit loading state and then success state`() = runTest {
        val fakeCatFact = Fact("Fake latest cat fact", 10, false)
        whenever(mockCatsUseCase.getLatestCatFactLocal()).thenReturn(
            CatFactRepository.CatFactResult.Success(
                fakeCatFact
            )
        )

        viewModel.getLatestCatFact()
        advanceUntilIdle() //since i'm using Dispatcher.IO coroutines

        viewModel.catFactState.test {
            assertEquals(CatFactRepository.CatFactResult.Success(fakeCatFact), awaitItem())
        }
    }

    @Test
    fun `getANewCatFact should update multipleCatsFound state based on fact content`() = runTest {
        val fakeCatFactWithCats = Fact("Many many fake cats", 19, false)
        whenever(mockCatFactRepository.getACatFact()).thenReturn(
            CatFactRepository.CatFactResult.Success(
                fakeCatFactWithCats
            )
        )
        whenever(mockCatFactLocalRepo.insertFact(any())).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Success()
        )
        whenever(
            mockCatsUseCase.catsCheck(
                CatFactRepository.CatFactResult.Success(
                    fakeCatFactWithCats
                )
            )
        ).thenReturn(true)

        viewModel.getANewCatFact()
        advanceUntilIdle() //since i'm using Dispatcher.IO coroutines

        assertTrue(viewModel.multipleCatsFound.value) // Ensure multipleCatsFound is true
    }

    @Test
    fun `getANewCatFact should save cat fact to local repository on success`() = runTest {
        val fakeCatFact = Fact("Save this fake cat fact", 10, false)
        whenever(mockCatFactRepository.getACatFact()).thenReturn(
            CatFactRepository.CatFactResult.Success(
                fakeCatFact
            )
        )
        whenever(mockCatFactLocalRepo.insertFact(any())).thenReturn(
            CatFactLocalRepo.LocalCatFactResult.Success()
        )

        viewModel.getANewCatFact()
        advanceUntilIdle() //since i'm using Dispatcher.IO coroutines

        verify(mockCatFactLocalRepo).insertFact(fakeCatFact) // Verify that the save function was called
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain() // Reset the dispatcher after the test
    }
}