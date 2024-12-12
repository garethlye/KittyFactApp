package jp.speakbuddy.edisonandroidexercise.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.base.CommonConstants
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class CatBreedTensorClassifier @Inject constructor(
    private val generalUtils: GeneralUtils
) {
    private var interpreter: Interpreter? = null
    private var labels: List<String>? = null

    fun init(context: Context) {
        val assetManager = context.assets
        val modelFile = loadModelFile(context)
        if (modelFile != null) {
            interpreter = Interpreter(modelFile)
            labels =
                assetManager.open(CommonConstants.CAT_MODEL_TENSOR_LABELS).bufferedReader().readLines()
        }
    }

    fun classify(context: Context, image: Bitmap): String {
        //Error handler check here uses a string rather than result
        if (interpreter != null && labels != null) {
            val inputBuffer = preprocessImage(image)
            val outputBuffer = Array(1) { FloatArray(labels!!.size) }
            interpreter!!.run(inputBuffer, outputBuffer)

            val maxIdx = outputBuffer[0].indices.maxByOrNull { outputBuffer[0][it] } ?: -1
            val confidence = outputBuffer[0][maxIdx]

            val catStatement = if (confidence < 0.90) {
                context.getString(R.string.txt_no_idea)
            } else {
                context.getString(R.string.txt_i_am_confident, generalUtils.convertFloatToPercentage(confidence), labels!![maxIdx])
            }
            return catStatement
        }
        return context.getString(R.string.failed_to_load_tensorflowlite)
    }

    private fun preprocessImage(image: Bitmap): ByteBuffer {
        /**
         * Honestly this function may be slightly useless, I am attempting to check if the image is
         * mostly a black screen to reduce the incorrect confidence levels of the tensor flow since
         * I did not train the model of anything except a cat / dog
         * This works sometimes so I'm not sure if I should keep this or not, but leaving it here.
         */
        //single image with 800x800 in RGB format(just a guesstimate of what the output is like)
        val inputBuffer = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        val scaledBitmap = Bitmap.createScaledBitmap(image, 224, 224, true)

        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = scaledBitmap.getPixel(x, y)
                inputBuffer.putFloat((Color.red(pixel) / 255.0).toFloat())
                inputBuffer.putFloat((Color.green(pixel) / 255.0).toFloat())
                inputBuffer.putFloat((Color.blue(pixel) / 255.0).toFloat())
            }
        }
        return inputBuffer
    }

    private fun loadModelFile(context: Context): ByteBuffer? {
        return try {
            val fileName = CommonConstants.CAT_MODEL_TENSORFLOWLITE
            val assetFileDescriptor = context.assets.openFd(fileName)

            val inputStream = assetFileDescriptor.createInputStream()
            val byteArray = inputStream.readBytes()
            inputStream.close()

            ByteBuffer.allocateDirect(byteArray.size).apply {
                order(ByteOrder.nativeOrder())
                put(byteArray)
                rewind()
            }
        } catch (e: Exception) {
            null
        }
    }
}