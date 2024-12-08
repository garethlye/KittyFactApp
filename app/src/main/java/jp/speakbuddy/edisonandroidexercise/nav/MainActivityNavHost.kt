package jp.speakbuddy.edisonandroidexercise.nav

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import jp.speakbuddy.edisonandroidexercise.base.NavigationAction
import jp.speakbuddy.edisonandroidexercise.ui.catDetector.CatDetectorScreen
import jp.speakbuddy.edisonandroidexercise.ui.fact.FactScreen
import jp.speakbuddy.edisonandroidexercise.ui.listAndSearch.KittyListAndSearch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainActivityNavHost(context: Context, navController: NavHostController) {
    NavHost(navController, startDestination = NavRouteNames.FACT_SCREEN) {
        composable(NavRouteNames.FACT_SCREEN,
            deepLinks = listOf(
                navDeepLink { uriPattern = "fact://catfactapp.com/fact_screen" }
            )) {
            FactScreen(
                onNavigateAction = { route ->
                    handleNavigationAction(context, navController, route)
                }
            )
        }
        composable(NavRouteNames.KITTY_LIST_AND_SEARCH,
            deepLinks = listOf(
                navDeepLink { uriPattern = "fact://catfactapp.com/fact_list" }
            )) {
            KittyListAndSearch(
                onNavigateAction = { route ->
                    handleNavigationAction(context, navController, route)
                }
            )
        }
        composable(NavRouteNames.CAT_DETECTOR_SCREEN,
            deepLinks = listOf(
                navDeepLink { uriPattern = "fact://catfactapp.com/cat_detector" }
            )) {
            CatDetectorScreen(
                onNavigateAction = { route ->
                handleNavigationAction(context, navController, route)
            },
                cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
            )
        }
    }
}

/**
 * Could also put this in a separate NavHost parent class if there are more Activities.
 * Since single activity i will put it here for easier visibility
 **/
fun handleNavigationAction(
    context: Context,
    navController: NavHostController,
    action: NavigationAction
) {
    when (action) {
        //Normally i would add error handling but each screen is independent so should be okay
        is NavigationAction.Back -> navController.popBackStack()
        is NavigationAction.ToScreen -> navController.navigate(action.screenName)
        is NavigationAction.ToSettings -> {
            val intent = Intent(
                action.settingType,
                Uri.fromParts("package", context.packageName, null)
            )
            context.startActivity(intent)
        }
    }
}