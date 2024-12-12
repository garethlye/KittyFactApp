package jp.speakbuddy.edisonandroidexercise.ui.fact

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.network.CatFactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FactScreenTest {
    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity> =
        createAndroidComposeRule(ComponentActivity::class.java)

    private lateinit var viewModel: CatFactViewModel
    private val mockIsLoading = MutableStateFlow(false)
    private val mockOfflineMode = MutableStateFlow(false)
    private val mockMultipleCatsFound = MutableStateFlow(false)
    private val mockNumberOfCats = MutableStateFlow(10)

    @Before
    fun setUp() {
        viewModel = mockk(relaxed = true)

        every { viewModel.isLoading } returns mockIsLoading
        every { viewModel.offlineMode } returns mockOfflineMode
        every { viewModel.multipleCatsFound } returns mockMultipleCatsFound
        every { viewModel.numberOfCatFacts } returns mockNumberOfCats
        every { viewModel.isInternetAvailable(any()) } returns true
        every { viewModel.isClickable() } returns true

        every { viewModel.catFactState } returns MutableStateFlow(
            CatFactRepository.CatFactResult.Success(
                Fact(fact = "A simulated looooong cat fact", length = 101)
            )
        )
    }

    @Test
    @DisplayName("Test: TopAppBar title is displayed")
    fun testTopAppBarTitleIsDisplayed() {
        composeTestRule.setContent {
            FactScreen(
                viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true)
            )
        }

        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.txt_cat_fact))
            .assertIsDisplayed()
    }

    @Test
    @DisplayName("Test: 'Get Fact' button is displayed and enabled")
    fun testGetFactButtonIsDisplayedAndEnabled() {
        composeTestRule.setContent {
            FactScreen(
                viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true)
            )
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.txt_get_fact))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    @DisplayName("Test: Creepy Cat Loader is shown when state = loading")
    fun testLoadingIndicatorIsDisplayed() {
        mockIsLoading.value = true

        composeTestRule.setContent {
            FactScreen(
                viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true)
            )
        }

        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.txt_catloader))
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    @DisplayName("Test: Checks that 'Get Fact' button calls getANewCatFact")
    fun testGetFactButtonClick() {
        mockIsLoading.value = false
        mockOfflineMode.value = false

        composeTestRule.setContent {
            FactScreen(
                viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true)
            )
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.txt_get_fact))
            .assertExists()
            .assertIsEnabled()
            .performClick()

        verify { viewModel.getANewCatFact() }
    }

    @Test
    @DisplayName("Test: Check if length: %d is displayed correctly")
    fun testFactLengthIsDisplayedForLongFacts() {
        //declaring length = 101 doesn't work since fact length is calculated separately.
        every { viewModel.catFactState } returns MutableStateFlow(
            CatFactRepository.CatFactResult.Success(
                Fact(
                    fact = "A simulated looooooooooooooooooooooooooooooooooooo" +
                            "oooooooooooooooooooooooooooooooooooooooong cat fact", length = 101
                )
            )
        )
        composeTestRule.setContent {
            FactScreen(
                viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true)
            )
        }
        if (viewModel.catFactState.value is CatFactRepository.CatFactResult.Success) {
            val lengthText = composeTestRule.activity.getString(
                R.string.txt_length,
                (viewModel.catFactState.value as CatFactRepository.CatFactResult.Success).data.length
            )
            composeTestRule.onNodeWithText(lengthText)
                .assertExists()
        } else {
            //this won't fire, only to satisfy missing status check error from UI test
            composeTestRule.onNodeWithText("Length: 101")
                .assertDoesNotExist()
        }
    }

    @Test
    @DisplayName("Test: Error Icon is Displayed in offline mode")
    fun testErrorIconIsDisplayed() {
        every { viewModel.offlineMode } returns MutableStateFlow(true)
        composeTestRule.setContent {
            FactScreen(
                viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true)
            )
        }

        composeTestRule.onNodeWithTag(
            composeTestRule.activity.getString(R.string.txt_offline_mode),
            useUnmergedTree = true
        )
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    @DisplayName("Test: Share button shown only when there is a fact")
    fun testShareIconIsDisplayed() {
        composeTestRule.setContent {
            FactScreen(
                viewModel = viewModel,
                onNavigateAction = mockk(relaxed = true)
            )
        }

        composeTestRule.onNodeWithTag(
            composeTestRule.activity.getString(R.string.txt_share_cat_fact),
            useUnmergedTree = true
        )
            .assertExists()
            .assertIsDisplayed()
    }
}