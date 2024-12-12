package jp.speakbuddy.edisonandroidexercise.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.ViewModel
import jp.speakbuddy.edisonandroidexercise.util.AppHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class BaseViewModel(
    val appHelper: AppHelper
) : BaseViewModelInt, ViewModel() {

    private val mIsLoading = MutableStateFlow(false)
    private val mOfflineMode = MutableStateFlow(false)

    override val isLoading: StateFlow<Boolean> get() = mIsLoading
    override val offlineMode: StateFlow<Boolean> get() = mOfflineMode

    /**
    I've contemplated whether to use my old style of onLoading(), onContent(), onError() used via VM,
    but that usually only works if you have a base layout that each screen inherits so that all
    loaders and error are the identical. It works with apps that have a universal design language
    but in this case it doesn't make a lot of sense with my use of composable.

    OR using UI states and each VM can listen to it, so that the state can control any custom styles
    of loaders and error displays.

    Since the main thing I use as custom is the loader, and the only error page is not a separate
    state error page, I am only using updateLoader function. Could probably improve but I can't
    justify it at the moment with the two screens app here.
     **/
    override fun updateLoader(showLoader: Boolean) {
        mIsLoading.value = showLoader
    }

    override fun isClickable(): Boolean {
        return appHelper.isClickable()
    }

    override fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun isInternetAvailable(context: Context): Boolean {
        /**
         * classic network check, may still throw an error if a network is connected but no
         * internet is available, but it depends on device manufacturer from my experience.
         * You could also change this to an active listener, but I don't want forced interruptions.
         */

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        val networkAvailable = if (networkCapabilities != null) {
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            false
        }
        mOfflineMode.value = !networkAvailable
        return networkAvailable
    }

    /**
     * I originally wanted to add in a deeplink attached to the string content for the share, but
     * without a 3rd party redirect to app store if it's not installed then I think it doesn't
     * make a lot of sense?
     * I've added a deeplink support for it anyway, links with "fact://catfactapp.com"
     */
    override fun shareCatFact(context: Context, factToShare: String) {
        appHelper.shareCatFact(context, factToShare)
    }

}