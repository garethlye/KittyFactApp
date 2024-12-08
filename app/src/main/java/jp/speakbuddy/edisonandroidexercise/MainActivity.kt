package jp.speakbuddy.edisonandroidexercise

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.speakbuddy.edisonandroidexercise.base.BaseActivity
import jp.speakbuddy.edisonandroidexercise.nav.MainActivityNavHost
import jp.speakbuddy.edisonandroidexercise.ui.theme.EdisonAndroidExerciseTheme

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EdisonAndroidExerciseTheme {
                val navController = rememberNavController()
                MainActivityNavHost(this, navController = navController)
            }
        }
    }
}