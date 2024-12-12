package jp.speakbuddy.edisonandroidexercise.base

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface BaseViewModelInt {
    /**
     * Like I explained in the other interfaces, this is mostly pointless but may be required for
     * unit testing purposes with Hilt. Since my Preview Composable(s) also require Interfaces to
     * generate a preview because of Hilt, this interface is also required.
     * Although I believe using interfaces does not break MVVM, there is no real benefit of using
     * it in this app in my opinion.
     */
    val isLoading: StateFlow<Boolean>
    val offlineMode: StateFlow<Boolean>
    fun shareCatFact(context: Context, factToShare: String)
    fun isInternetAvailable(context: Context): Boolean
    fun isClickable(): Boolean
    fun updateLoader(showLoader: Boolean)
    fun showToast(context: Context, message: String)
}