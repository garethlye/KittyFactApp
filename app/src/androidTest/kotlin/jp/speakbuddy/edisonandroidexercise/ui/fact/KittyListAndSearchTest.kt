package jp.speakbuddy.edisonandroidexercise.ui.fact

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal
import jp.speakbuddy.edisonandroidexercise.ui.listAndSearch.KittyListAndSearch
import jp.speakbuddy.edisonandroidexercise.ui.listAndSearch.KittyListAndSearchViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KittyListAndSearchTest {
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity> =
        createAndroidComposeRule(ComponentActivity::class.java)

    private lateinit var viewModel: KittyListAndSearchViewModel
    private val navController = mockk<NavController>(relaxed = true)
    private val mockedHashed = "8f5302375bb3edb93a2c4a87e67c1361c3f21aff01e96768e758bb882125788f"
    private val mockCatErrorMessage = MutableStateFlow("")

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
        every { viewModel.isClickable() } returns true
        every { viewModel.catErrorMessage } returns mockCatErrorMessage
    }

    @Test
    @DisplayName("Test: Back button functionality test")
    fun testBackButtonNavigatesBack() {
        composeTestRule.setContent {
            KittyListAndSearch(viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true))
        }

        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.txt_back))
            .performClick()
        verify { navController.popBackStack() }
    }

    @Test
    @DisplayName("Test: Toggle favourite list")
    fun testToggleFavouritesButton() {
        composeTestRule.setContent {
            KittyListAndSearch(viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true))
        }

        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.txt_filter_favourites))
            .performClick()

        verify { viewModel.getAllFavouriteCatFacts() }
    }

    @Test
    @DisplayName("Test: Delete Popup Displayed")
    fun testDeletePopupDisplayedOnClick() {
        composeTestRule.setContent {
            KittyListAndSearch(viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true))
        }

        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.txt_delete_facts))
            .performClick()

        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.txt_delete_facts))
            .assertIsDisplayed()
    }

    @Test
    @DisplayName("Test: Search facts calls search function")
    fun testSearchUpdatesViewModel() {
        val searchQuery = "funny cats"
        composeTestRule.setContent {
            KittyListAndSearch(viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true))
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.txt_search_facts))
            .performTextInput(searchQuery)
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.txt_search_facts))
            .performClick()

        verify { viewModel.searchCatFacts(searchQuery) }
    }

    @Test
    @DisplayName("Test: Length displayed for a row in results and length is correct")
    fun testCatFactLengthIsDisplayed() {
        val fact = "This is a very long cat fact"
        val longFact = CatFactLocal(
            fact = fact,
            length = 120,
            isFavourite = false,
            factHashKey = mockedHashed
        )
        val pagingData = PagingData.from(listOf(longFact))
        val pagingDataFlow = MutableStateFlow(pagingData)

        every { viewModel.mCatFactsFromLocalDb } returns pagingDataFlow

        composeTestRule.setContent {
            KittyListAndSearch(viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true))
        }

        composeTestRule.onNodeWithText("Length: ${longFact.length}")
            .assertIsDisplayed()
    }

    @Test
    @DisplayName("Test: Cat Fact Popup displayed when selected")
    fun testFactPopupDisplaysSelectedFact() {
        val fact = "This is a cat fact for the popup."
        val catFact = CatFactLocal(
            fact = fact,
            length = fact.length,
            isFavourite = false,
            factHashKey = mockedHashed
        )

        val pagingData = PagingData.from(listOf(catFact))
        val pagingDataFlow = MutableStateFlow(pagingData)

        every { viewModel.mCatFactsFromLocalDb } returns pagingDataFlow

        composeTestRule.setContent {
            KittyListAndSearch(viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true))
        }

        composeTestRule.onNodeWithText(fact).performClick()
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.txt_favourite_cat))
            .assertIsDisplayed()
    }

    @Test
    @DisplayName("Test: Toggle favourite list")
    fun testDeleteAllFactsButtonDeletesAllFacts() {
        val fact = "Mocked fact"
        val mockCatFact = CatFactLocal(
            fact = fact,
            length = 9,
            isFavourite = false,
            factHashKey = mockedHashed
        )

        val pagingData = PagingData.from(listOf(mockCatFact))
        val pagingDataFlow = MutableStateFlow(pagingData)

        every { viewModel.mCatFactsFromLocalDb } returns pagingDataFlow

        composeTestRule.setContent {
            KittyListAndSearch(viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true))
        }

        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.txt_delete_facts))
            .performClick()
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.txt_delete_all_fact))
            .performClick()

        verify { viewModel.deleteAllCatFacts() }
    }
}