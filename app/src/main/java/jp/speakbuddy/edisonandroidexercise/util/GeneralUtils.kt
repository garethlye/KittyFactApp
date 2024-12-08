package jp.speakbuddy.edisonandroidexercise.util

import android.annotation.SuppressLint
import javax.inject.Inject

class GeneralUtils {
    @SuppressLint("DefaultLocale")
    fun convertFloatToPercentage(confidence: Float): String {
        return String.format("%.2f", confidence * 100)
    }
}