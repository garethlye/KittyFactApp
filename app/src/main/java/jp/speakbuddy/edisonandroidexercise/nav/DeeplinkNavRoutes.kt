package jp.speakbuddy.edisonandroidexercise.nav

object DeeplinkNavRoutes {
    /**
     * Typically I would always create a deep link handler class of sorts to handle redirections
     * correctly, since there can be additional params added into the deeplink especially for
     * marketing deep links or external app interactions or third party services like branch.io,
     * appflyer, firebase, etc. These are usually paired with analytics services as well. Since
     * it's not really required in this app, I have opted not to create one and just use constant
     * nav routes here since the only application i'm using this for is Quick Shortcuts.
     */
    const val FACT_SCREEN = "fact://catfactapp.com/fact_screen"
    const val KITTY_LIST_AND_SEARCH = "fact://catfactapp.com/fact_list"
    const val CAT_DETECTOR_SCREEN = "fact://catfactapp.com/cat_detector"
}