package jp.speakbuddy.edisonandroidexercise.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.base.CommonConstants
import java.io.File
import java.io.FileOutputStream

class AppHelper {

    private var lastClickTime = 0L

    //Just a simple method to prevent over-clicking a button
    fun isClickable(): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 500) {  // 500ms delay
            lastClickTime = currentTime
            return true
        }
        return false
    }

    fun shareCatFact(context: Context, fact: String) {
        val imageUri = getCatDrawableUri(context)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, fact)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.txt_share_cat_fact)
            )
        )
    }

    //since you can't share files as an image directly, this converts it to a bitmap
    //currently hardcoded to a specific drawable since it's only used in a single function
    private fun getCatDrawableUri(context: Context): Uri? {
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_cat_sitting)
        if (drawable != null) {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, CommonConstants.CAT_MODEL_SHARE_IMG)
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }
        return null
    }
}