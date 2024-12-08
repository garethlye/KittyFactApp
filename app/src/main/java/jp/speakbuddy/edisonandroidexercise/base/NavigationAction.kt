package jp.speakbuddy.edisonandroidexercise.base

sealed class NavigationAction {
    object Back : NavigationAction()
    data class ToScreen(val screenName: String) : NavigationAction()
    data class ToSettings(val settingType: String) : NavigationAction()
}