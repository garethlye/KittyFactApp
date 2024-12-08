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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.ui.catDetector.CatDetectorScreen
import jp.speakbuddy.edisonandroidexercise.ui.catDetector.CatDetectorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPermissionsApi::class)
@RunWith(AndroidJUnit4::class)
class CatDetectorScreenTest {

    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity> =
        createAndroidComposeRule(ComponentActivity::class.java)

    private lateinit var mockViewModel: CatDetectorViewModel
    private val mockBreedName = MutableStateFlow("")

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.breedName } returns mockBreedName
    }

    @Test
    fun testCameraPermissionGranted() {
        val mockPermissionState = mockk<PermissionState>(relaxed = true) {
            every { permission } returns android.Manifest.permission.CAMERA
            every { status } returns PermissionStatus.Granted
            every { launchPermissionRequest() } just Runs
        }

        composeTestRule.setContent {
            CatDetectorScreen(
                viewModel = mockViewModel,
                onNavigateAction = mockk(relaxed = true),
                cameraPermissionState = mockPermissionState
            )
        }

        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.txt_is_it_a_cat))
            .assertExists()
            .assertIsEnabled()

        composeTestRule.onNodeWithTag(composeTestRule.activity.getString(R.string.android_view))
            .assertExists()
    }

    @Test
    fun testCameraPermissionDenied() {
        val deniedStatus = mockk<PermissionStatus.Denied>(relaxed = true) {
            every { shouldShowRationale } returns false
        }
        val mockPermissionState = mockk<PermissionState>(relaxed = true) {
            every { permission } returns android.Manifest.permission.CAMERA
            every { status } returns deniedStatus
            every { launchPermissionRequest() } just Runs
        }
        composeTestRule.setContent {
            CatDetectorScreen(
                viewModel = mockViewModel,
                onNavigateAction = mockk(relaxed = true),
                cameraPermissionState = mockPermissionState
            )
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.txt_permission_required))
            .assertExists()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.txt_open_settings))
            .assertExists()
            .assertIsEnabled()
    }

    @Test
    fun testIsItACatButtonClick() {
        val mockPermissionState = mockk<PermissionState>(relaxed = true) {
            every { permission } returns android.Manifest.permission.CAMERA
            every { status } returns PermissionStatus.Granted
            every { launchPermissionRequest() } just Runs
        }
        composeTestRule.setContent {
            CatDetectorScreen(
                viewModel = mockViewModel,
                onNavigateAction = mockk(relaxed = true),
                cameraPermissionState = mockPermissionState
            )
        }

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.txt_is_it_a_cat))
            .assertExists()
            .assertIsEnabled()
            .performClick()

        verify { mockViewModel.captureAndClassify(any()) }
    }

    @Test
    fun testCatClassificationOverlay() {
        val mockPermissionState = mockk<PermissionState>(relaxed = true) {
            every { permission } returns android.Manifest.permission.CAMERA
            every { status } returns PermissionStatus.Granted
            every { launchPermissionRequest() } just Runs
        }
        every { mockViewModel.breedName } returns MutableStateFlow("Cat")

        composeTestRule.setContent {
            CatDetectorScreen(
                viewModel = mockViewModel,
                onNavigateAction = mockk(relaxed = true),
                cameraPermissionState = mockPermissionState
            )
        }

        composeTestRule.onNodeWithText("Cat")
            .assertExists()
            .assertIsDisplayed()
    }
}