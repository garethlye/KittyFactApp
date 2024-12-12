package jp.speakbuddy.edisonandroidexercise.ui.fact

import android.content.Context
import android.content.res.Configuration
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.base.NavigationAction
import jp.speakbuddy.edisonandroidexercise.model.Fact
import jp.speakbuddy.edisonandroidexercise.nav.NavRouteNames
import jp.speakbuddy.edisonandroidexercise.network.CatFactRepository
import jp.speakbuddy.edisonandroidexercise.util.CatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun FactScreen(
    viewModel: CatFactViewModelInt = hiltViewModel<CatFactViewModel>(),
    onNavigateAction: (NavigationAction) -> Unit
) {
    val context = LocalContext.current
    val catFactState by viewModel.catFactState.collectAsState()
    val numberOfFactsSeen by viewModel.numberOfCatFacts.collectAsState()
    val multipleCatsFound by viewModel.multipleCatsFound.collectAsState()
    val offlineMode by viewModel.offlineMode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isClickable = viewModel::isClickable
    val getANewCatFactClick = viewModel::getANewCatFact
    val getARandomCatFactClick = viewModel::getARandomLocalCatFact
    val mediaPlayer = rememberMediaPlayer(LocalContext.current, R.raw.mc_button_press)

    LaunchedEffect(Unit) {
        //launch effect with Unit object so it loads the latest cat fact once
        viewModel.getLatestCatFact()
        viewModel.getLatestCatFactNumber()
    }
    MainScreen(
        numberOfFactsSeen,
        catFactState,
        multipleCatsFound,
        mediaPlayer,
        offlineMode,
        isLoading,
        getANewCatFactClick,
        isClickable,
        getARandomCatFactClick,
        { showLoader -> viewModel.updateLoader(showLoader) },
        { viewModel.isInternetAvailable(context) },
        { message -> viewModel.showToast(context, message) },
        { catFact -> viewModel.shareCatFact(context, catFact) },
        onNavigateAction
    )
}

/**
 * I was considering passing in Modifier for most composable(s) to make it reusable, but in this
 * case I think it's not necessary since I don't reuse any composable between screens or states.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    numberOfFactsSeen: Int,
    catFactState: CatFactRepository.CatFactResult<Fact>,
    multipleCatsFound: Boolean,
    mediaPlayer: MediaPlayer?,
    offlineMode: Boolean,
    isLoading: Boolean,
    getANewCatFactClick: () -> Unit,
    isClickable: () -> Boolean,
    getARandomCatFactClick: () -> Unit,
    updateLoader: (Boolean) -> Unit,
    isInternetAvailable: () -> Boolean,
    showToast: (String) -> Unit,
    shareCatFact: (String) -> Unit,
    onNavigateAction: (NavigationAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.txt_cat_fact),
                            modifier = Modifier.testTag(stringResource(R.string.txt_cat_fact)))
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_normal)))
                        Text(
                            text = stringResource(
                                R.string.txt_number_of_facts_seen,
                                numberOfFactsSeen
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.Black
                ),
                actions = {
                    IconButton(onClick = {
                        if (isClickable()) {
                            onNavigateAction(NavigationAction.ToScreen(NavRouteNames.CAT_DETECTOR_SCREEN))
                        }
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_camera),
                            contentDescription = stringResource(R.string.txt_detect_a_cat),
                            tint = Color.Unspecified
                        )
                    }
                    IconButton(onClick = {
                        if (isClickable()) {
                            onNavigateAction(NavigationAction.ToScreen(NavRouteNames.KITTY_LIST_AND_SEARCH))
                        }
                    }) {
                        Icon(
                            //made my own cat icon, tried to follow as close to material design
                            painterResource(id = R.drawable.ic_search),
                            contentDescription = stringResource(R.string.txt_search_facts),
                            tint = Color.Unspecified
                        )
                    }
                }
            )
        }) { scaffoldPadding ->

        val isPortrait =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_large))
        ) {
            if (isPortrait) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(scaffoldPadding)
                        .padding(dimensionResource(R.dimen.padding_normal)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        space = dimensionResource(R.dimen.padding_normal)
                    )
                ) {
                    CommonUIs(
                        catFactState,
                        multipleCatsFound,
                        mediaPlayer,
                        offlineMode,
                        getANewCatFactClick,
                        getARandomCatFactClick,
                        isClickable,
                        isInternetAvailable,
                        showToast,
                        isLoading,
                        updateLoader,
                        shareCatFact
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(scaffoldPadding)
                ) {
                    CommonUIs(
                        catFactState,
                        multipleCatsFound,
                        mediaPlayer,
                        offlineMode,
                        getANewCatFactClick,
                        getARandomCatFactClick,
                        isClickable,
                        isInternetAvailable,
                        showToast,
                        isLoading,
                        updateLoader,
                        shareCatFact
                    )
                }
            }
        }
    }
}

@Composable
private fun CommonUIs(
    catFactState: CatFactRepository.CatFactResult<Fact>,
    multipleCatsFound: Boolean,
    mediaPlayer: MediaPlayer?,
    offlineMode: Boolean,
    getANewCatFactClick: () -> Unit,
    getARandomCatFactClick: () -> Unit,
    isClickable: () -> Boolean,
    isInternetAvailable: () -> Boolean,
    showToast: (String) -> Unit,
    isLoading: Boolean,
    updateLoader: (Boolean) -> Unit,
    shareCatFact: (String) -> Unit
) {
    val catFactText = remember { mutableStateOf("") }
    val over100Chars = remember { mutableStateOf(false) }
    val noMoreCatFactsTxt = stringResource(R.string.txt_no_stored_facts)

    DisposableEffect(Unit) {
        onDispose {
            //need to make sure media player is released when screen is gone
            mediaPlayer?.release()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(dimensionResource(R.dimen.size_200))
    ) {
        CatLoaderLottieAnimation(
            isPlaying = isLoading
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = stringResource(R.string.txt_fact),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_normal))
        )


        Text(
            text = catFactText.value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )


        when (catFactState) {
            is CatFactRepository.CatFactResult.Success -> {
                val fact =
                    catFactState.data.fact
                catFactText.value = fact
                over100Chars.value = fact.length > 100
                updateLoader(false)
            }

            is CatFactRepository.CatFactResult.Error -> {
                updateLoader(false)
                if (!catFactState.message.isNullOrEmpty()) {
                    catFactText.value = catFactState.message
                }
                over100Chars.value = false
                //maybe change colour to red, but it's ugly
            }

            is CatFactRepository.CatFactResult.Loading -> {
                updateLoader(true)
            }
        }

        val onGetFactClick = {
            //I know !! is bad, but in this case shouldn't be an issue..
            if (!mediaPlayer?.isPlaying!!) {
                mediaPlayer.start()
            }
            if (isInternetAvailable()) {
                getANewCatFactClick()
            } else {
                if (catFactState is CatFactRepository.CatFactResult.Error &&
                    catFactState.message == CatsUseCase.CatFactStatus.NO_CATS.message
                ) {
                    //i feel like there might be a better way than a string compare, like error code
                    //but I wanted to re-use Result.Error as it's technically an error that occurred
                    showToast(noMoreCatFactsTxt)
                } else {
                    //while offline I want to retain some functionality, show a random offline fact
                    getARandomCatFactClick()
                }
            }
        }

        if (multipleCatsFound) {
            Text(
                text = stringResource(R.string.txt_multiple_cats),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_very_small))
            )
        }

        if (over100Chars.value) {
            Text(
                text = stringResource(R.string.txt_length, catFactText.value.length),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_very_small))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (offlineMode) {
                IconButton(
                    onClick = { /* ignoring */ }
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_offline),
                        contentDescription = stringResource(R.string.txt_offline_mode),
                        tint = Color.Red,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_tiny))
                            .testTag(stringResource(R.string.txt_offline_mode))
                    )
                }

            } else {
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.size_48)))
            }
            Button(
                onClick = {
                    if (isClickable()) {
                        onGetFactClick()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = dimensionResource(R.dimen.padding_very_small),
                        end = dimensionResource(R.dimen.padding_very_small)
                    )
            ) {
                Text(text = stringResource(R.string.txt_get_fact))
            }

            if (catFactState is CatFactRepository.CatFactResult.Success
                && catFactText.value.isNotEmpty()
            ) {
                Box {
                    IconButton(
                        onClick = {
                            shareCatFact(catFactText.value)
                        }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_share),
                            contentDescription = stringResource(R.string.txt_share_cat_fact),
                            tint = Color.Unspecified,
                            modifier = Modifier.testTag(stringResource(R.string.txt_share_cat_fact))
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.size_48)))
            }
        }
    }
}

@Composable
private fun rememberMediaPlayer(context: Context, resId: Int): MediaPlayer? {
    val isInPreview = isInPreview()
    return remember {
        if (!isInPreview) {
            MediaPlayer.create(context, resId)
        } else {
            null
        }
    }
}

/**
 * Personally I love using Lottie animations compared to raw video or GIFs.
 * I adapted the lottie animation as a cute cat display & a loading animation.
 */
@Composable
private fun CatLoaderLottieAnimation(isPlaying: Boolean = false) {
    if (!isInPreview()) {
        val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.creepy_cat_loader))

        LottieAnimation(
            composition = lottieComposition,
            iterations = if (isPlaying) LottieConstants.IterateForever else 1,
            modifier = Modifier.testTag(stringResource(R.string.txt_catloader))
        )
    }
}

/**
I created the ViewModelInt mainly for composable preview, not to use directly, sadly I couldn't
figure out a good way to deal with hilt injection of viewModel to render the entire screen.
But if you're previewing individual composable it's fine without this as it's decoupled from the VM,
normally i would not necessarily render every screen but i'm just showing how I would do it here.
 **/
object PreviewCatFactViewModel : CatFactViewModelInt {
    override fun getANewCatFact() {}
    override fun getARandomLocalCatFact() {}
    override fun getLatestCatFact() {}
    override fun getLatestCatFactNumber() {}
    override val catFactState: StateFlow<CatFactRepository.CatFactResult<Fact>>
        get() = MutableStateFlow(
            CatFactRepository.CatFactResult.Success(
                Fact(
                    "Cats have whiskers!",
                    19,
                    false
                )
            )
        )
    override val multipleCatsFound: StateFlow<Boolean> get() = MutableStateFlow(true)
    override val numberOfCatFacts: StateFlow<Int> get() = MutableStateFlow(12)
    override val isLoading: StateFlow<Boolean> get() = MutableStateFlow(false)
    override val offlineMode: StateFlow<Boolean> get() = MutableStateFlow(true)
    override fun shareCatFact(context: Context, factToShare: String) {}
    override fun isInternetAvailable(context: Context): Boolean { return true }
    override fun isClickable(): Boolean { return true }
    override fun updateLoader(showLoader: Boolean) {}
    override fun showToast(context: Context, message: String) {}
}

@Preview(showBackground = true)
@Composable
fun PreviewFactScreen() {
    val mockCatFactState =
        CatFactRepository.CatFactResult.Success(Fact("Cats have whiskers!", 101, false))
    val mockMultipleCatsFound = true
    val mockOfflineMode = true
    val mockIsLoading = false
    val mockNumberOfFactsSeen = 123

    val onNavigateAction: (NavigationAction) -> Unit = {}
    val getANewCatFactClick: () -> Unit = {}
    val getARandomCatFactClick: () -> Unit = {}
    val updateLoader: (Boolean) -> Unit = {}
    val isInternetAvailable: () -> Boolean = { true }
    val showToast: (String) -> Unit = {}
    val shareCatFact: (String) -> Unit = {}

    val isClickable: () -> Boolean = { true }

    FactScreen(
        onNavigateAction = onNavigateAction,
        viewModel = PreviewCatFactViewModel
    )

    MainScreen(
        numberOfFactsSeen = mockNumberOfFactsSeen,
        catFactState = mockCatFactState,
        multipleCatsFound = mockMultipleCatsFound,
        mediaPlayer = null,
        offlineMode = mockOfflineMode,
        isLoading = mockIsLoading,
        getANewCatFactClick = getANewCatFactClick,
        isClickable = isClickable,
        getARandomCatFactClick = getARandomCatFactClick,
        updateLoader = updateLoader,
        isInternetAvailable = isInternetAvailable,
        showToast = showToast,
        shareCatFact = shareCatFact,
        onNavigateAction = onNavigateAction
    )
}

@Composable
fun isInPreview(): Boolean {
    return LocalInspectionMode.current
}
