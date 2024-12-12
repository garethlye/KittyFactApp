package jp.speakbuddy.edisonandroidexercise.ui.catDetector

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.base.BaseViewModel
import jp.speakbuddy.edisonandroidexercise.util.AppHelper
import jp.speakbuddy.edisonandroidexercise.util.CatBreedTensorClassifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CatDetectorViewModel @Inject
constructor(
    appHelper: AppHelper,
    private val catBreedClassifier: CatBreedTensorClassifier,
) : BaseViewModel(appHelper) {

    private var imageCapture: ImageCapture? = null

    private val mBreedName = MutableStateFlow<String?>(null)
    val breedName: StateFlow<String?> = mBreedName.asStateFlow()

    fun initCatBreedTensorClassifier(context: Context) {
        catBreedClassifier.init(context)
    }

    fun setImageCapture(imageCapture: ImageCapture) {
        this.imageCapture = imageCapture
    }

    fun captureAndClassify(context: Context) {
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        try {
                            ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(file)
                            ) { decoder, _, _ ->
                                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    } else {
                        BitmapFactory.decodeFile(file.absolutePath)
                    }
                    mBreedName.value =
                        if (bitmap != null) {
                            catBreedClassifier.classify(context, bitmap)
                        } else {
                            context.getString(R.string.failed_to_load_tensorflowlite)
                        }
                }

                override fun onError(exception: ImageCaptureException) {
                    mBreedName.value = exception.message
                    exception.printStackTrace()
                }
            }
        )
    }
}