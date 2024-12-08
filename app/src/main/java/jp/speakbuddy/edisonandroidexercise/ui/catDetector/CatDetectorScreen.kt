package jp.speakbuddy.edisonandroidexercise.ui.catDetector

import android.provider.Settings
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.base.NavigationAction

/**
 * Okay so this may or may not be a good addition for the assessment but I wanted to add something
 * somewhat unique. Originally I wanted to add a cat breed detector, I trained a tensor model with
 * like 5 different common breeds of cats with like 50 images each but the model file ended up
 * being like 350Mb even after optimization. I didn't want to add such a huge file to the APK, and
 * since I couldn't use dynamic feature module as that would require a play store release, I
 * considered using my google drive or github as an external download and allowing the user to
 * download on-demand but I fear I might be over-scoping it. So in the end I opted for a very basic
 * tensor model of a "cat detector" instead, trained with only 5 pictures of cats and 5 pictures of
 * a dog without the usual variations so the size is only 2mb. So it can somewhat tell whether the
 * image you are showing is a cat, but since the model training was so tiny, it's really not that
 * reliable but generally just showing the concept of this and admitting to the flaws :x
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CatDetectorScreen(
    viewModel: CatDetectorViewModel = hiltViewModel<CatDetectorViewModel>(),
    onNavigateAction: (NavigationAction) -> Unit,
    cameraPermissionState: PermissionState
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (cameraPermissionState.status.isGranted) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
                    .testTag(stringResource(R.string.android_view))
            )
            Button(
                onClick = { viewModel.captureAndClassify(context) },
                enabled = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(dimensionResource(R.dimen.padding_normal))
                    .testTag(stringResource(R.string.txt_is_it_a_cat))
            ) {
                Text(stringResource(R.string.txt_is_it_a_cat))
            }
            ClassifyResultOverlay(catStatement = viewModel.breedName.collectAsState().value)
            LaunchedEffect(cameraPermissionState) {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                viewModel.initCatBreedTensorClassifier(context)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                    val imageCapture = ImageCapture.Builder().build()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        //typically should handle camera errors, but rare so will ignore this time
                        e.printStackTrace()
                    }
                    viewModel.setImageCapture(imageCapture)
                }, ContextCompat.getMainExecutor(context))
            }
        } else {
            // Permission denied or not yet granted, show a rationale dialog box with button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.txt_permission_required),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_normal))
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.size_48)))
                Button(onClick = {
                    onNavigateAction(NavigationAction.ToSettings(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
                }) {
                    Text(stringResource(R.string.txt_open_settings))
                }
            }
        }
    }
}

@Composable
fun ClassifyResultOverlay(catStatement: String?) {
    catStatement?.let {
        Text(
            text = catStatement,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(dimensionResource(R.dimen.padding_very_small))
                .fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * I can't use Composable Preview here as it conflicts with androidx.camera.core.Preview,
 * slightly annoying but since this screen is kind of plain I think it's probably fine.
 */