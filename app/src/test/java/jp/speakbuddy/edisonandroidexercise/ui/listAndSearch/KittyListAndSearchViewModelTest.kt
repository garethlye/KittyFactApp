package jp.speakbuddy.edisonandroidexercise.ui.listAndSearch

import androidx.paging.PagingData
import androidx.paging.map
import jp.speakbuddy.edisonandroidexercise.data.CatFactLocalRepo
import jp.speakbuddy.edisonandroidexercise.data.CatFactLocalRepo.LocalCatFactResult
import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class KittyListAndSearchViewModelTest {
    private lateinit var viewModel: KittyListAndSearchViewModel
    private lateinit var mockRepository: CatFactLocalRepo
    private val testDispatcher = UnconfinedTestDispatcher()

    /**
     * Due to the usage of PagingData, each unit tests need to be run individually for it to pass.
     * Not ideal, especially with my use of CI/CD action that runs units tests so in a production
     * variant of this this needs to be modified in some way that makes sense. Maaaybe I shouldn't
     * of use mCatFactsFromLocalDb as a flow of paging data.
     */
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()

        viewModel = KittyListAndSearchViewModel(
            appHelper = mock(),
            repository = mockRepository,
            ioDispatcher = testDispatcher
        )
        val initialCatFact = CatFactLocal(
            id = 1,
            fact = "Cats are awesome",
            isFavourite = false,
            factHashKey = "123",
            length = 12
        )
        viewModel.mCatFactsFromLocalDb = MutableStateFlow(PagingData.from(listOf(initialCatFact)))
    }

    @Test
    fun `test initial state of currentQueryType is ALL`() = runTest {
        assertEquals(KittyListAndSearchViewModel.QueryType.ALL, viewModel.currentQueryType.first())
    }

    @Test
    fun `test getAllStoredCatFacts updates currentQueryType to ALL`() = runTest {
        viewModel.getAllStoredCatFacts()

        assertEquals(KittyListAndSearchViewModel.QueryType.ALL, viewModel.currentQueryType.first())
    }

    @Test
    fun `test getAllFavouriteCatFacts updates currentQueryType to FAVOURITES`() = runTest {
        viewModel.getAllFavouriteCatFacts()

        assertEquals(
            KittyListAndSearchViewModel.QueryType.FAVOURITES,
            viewModel.currentQueryType.first()
        )
    }

    @Test
    fun `test searchCatFacts updates currentQueryType to SEARCH and updates search query`() =
        runTest {
            val query = "cute cats"

            viewModel.searchCatFacts(query)

            assertEquals(
                KittyListAndSearchViewModel.QueryType.SEARCH,
                viewModel.currentQueryType.first()
            )
            assertEquals(query, viewModel.mCurrentSearchQuery.first())
        }

    @Test
    fun `test deleteAllCatFacts calls repository deleteAllFacts`() = runTest {
        whenever(mockRepository.deleteAllFacts()).thenReturn(LocalCatFactResult.Success())

        viewModel.deleteAllCatFacts()

        verify(mockRepository).deleteAllFacts()
    }

    @Test
    fun `test mForceSearchTrigger triggers search when currentSearchQuery is updated`() = runTest {
        val query = "funny cats"

        viewModel.searchCatFacts(query)

        assertEquals(query, viewModel.mCurrentSearchQuery.first())
        assertEquals(Unit, viewModel.mForceSearchTrigger.first())
    }

    /**
     * Okay the two tests below will most likely fail. I opted for pagination with state flow for
     * the listing to handle every query type(mCatFactsFromLocalDb). It's compact and precise, but
     * either i'm not intelligent enough to find a way to actually get the proper output from the
     * pagination flow, or there really isn't a way to get it from unit tests :(
     * I've tried to "map" and "collect" the data but i'm always receiving a difference reference,
     * or I can't extract the content itself to compare either. Changing the testDispatcher
     * "SOMETIMES" works but it's not consistent or other unit tests will fail.
     * The only way I could properly do it is to modify mCatFactsFromLocalDb function calls like
     * getAllFacts to also update a separate list in the VM before returning the stateflow list
     * and the unit tests run off that separate list, but that kind of pollutes the VM with
     * unnecessary code and uses more ram by creating unnecessary variables with each query.
     * I admit defeat for now.
     */
    @Test
    fun `test toggleFavoriteStatus calls repository updateCatFact and updates catFact state`() =
        runTest {
            val catFact = CatFactLocal(
                id = 1,
                fact = "Cats are awesome",
                isFavourite = false,
                factHashKey = "123",
                length = 12
            )

            whenever(mockRepository.getAllFacts()).thenReturn(flowOf(PagingData.from(listOf(catFact))))

            viewModel.getAllStoredCatFacts()

            val updatedCatFact = catFact.copy(isFavourite = true)

            whenever(mockRepository.updateCatFact(updatedCatFact)).thenAnswer {
                viewModel.mCatFactsFromLocalDb =
                    MutableStateFlow(PagingData.from(listOf(updatedCatFact)))
                Unit
            }
            /*
            whenever(mockRepository.updateCatFact(updatedCatFact)).thenAnswer {
                viewModel.mCatFactsFromLocalDb.value = PagingData.from(listOf(updatedCatFact))
                Unit
            }
            */
            viewModel.toggleFavoriteStatus(catFact)
            advanceUntilIdle()
            viewModel.getAllStoredCatFacts()
            advanceUntilIdle()

            verify(mockRepository).updateCatFact(updatedCatFact)

            val collectedData = mutableListOf<CatFactLocal>()
            //viewModel.mCatFactsFromLocalDb.first().map { collectedData.add(it) }
            viewModel.mCatFactsFromLocalDb.first().map {
                collectedData.add(it)
            }
            viewModel.mCatFactsFromLocalDb.collect { catFactStored ->
                catFactStored.map {
                    collectedData.add(it)
                    assertEquals(true, it)
                }
            }

            assertEquals(true, collectedData.first().isFavourite)
        }

    @Test
    fun `test updateSingleFactState updates catFact in mCatFactsFromLocalDb`() = runTest {
        val catFact = CatFactLocal(
            id = 1,
            fact = "Cats are awesome",
            isFavourite = false,
            factHashKey = "123",
            length = 12
        )

        whenever(mockRepository.getAllFacts()).thenReturn(flowOf(PagingData.from(listOf(catFact))))
        viewModel.getAllStoredCatFacts()
        advanceUntilIdle()

        viewModel.updateSingleFactState(catFact.copy(isFavourite = true))
        advanceUntilIdle()

        val collectedData = mutableListOf<CatFactLocal>()
        viewModel.mCatFactsFromLocalDb.first().map { collectedData.add(it) }

        assertEquals(true, collectedData.first().isFavourite)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
}