package jp.speakbuddy.edisonandroidexercise.ui.listAndSearch

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import jp.speakbuddy.edisonandroidexercise.R
import jp.speakbuddy.edisonandroidexercise.base.NavigationAction
import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KittyListAndSearch(
    viewModel: KittyListAndSearchViewModelInt = hiltViewModel<KittyListAndSearchViewModel>(),
    onNavigateAction: (NavigationAction) -> Unit
) {
    /**
     * NOTE: Somehow I couldn't reference default Icons import from .Material3, i'm not sure why
     * so I used .Material instead since they're just default icons.
     */
    val catFacts = viewModel.mCatFactsFromLocalDb.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }
    var isDeleteCatPopupVisible by remember { mutableStateOf(false) }
    var showOnlyFavourites by remember { mutableStateOf(false) }
    val isShowCatFactPopupVisible = remember { mutableStateOf(false) }
    val selectedCatFact = remember { mutableStateOf("") }
    val getAllFavouriteCatFactsClick = viewModel::getAllFavouriteCatFacts
    val getAllStoredCatFactsClick = viewModel::getAllStoredCatFacts
    val searchCatFactsClick = viewModel::searchCatFacts
    val toggleFavoriteStatusClick = viewModel::toggleFavoriteStatus
    val deleteAllCatFactsClick = viewModel::deleteAllCatFacts
    val errorMessage = viewModel.catErrorMessage.collectAsState()
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        //launch effect with Unit object so it loads all facts once on launch
        viewModel.getAllStoredCatFacts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.txt_saved_cat_fact)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.Black
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigateAction(NavigationAction.Back)
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.txt_back),
                            tint = Color.Unspecified
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (!isDeleteCatPopupVisible) {
                            isDeleteCatPopupVisible = true
                        }
                    }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.txt_delete_facts),
                            tint = Color.Unspecified
                        )
                    }

                    IconButton(onClick = {
                        showOnlyFavourites = !showOnlyFavourites
                        if (showOnlyFavourites) {
                            getAllFavouriteCatFactsClick()
                        } else {
                            getAllStoredCatFactsClick()
                        }
                    }) {
                        Icon(
                            imageVector = if (showOnlyFavourites) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = stringResource(R.string.txt_filter_favourites),
                            tint = if (showOnlyFavourites) Color.Red else Color.Black
                        )
                    }
                }
            )
        }) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_tiny)),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text(stringResource(R.string.txt_search_facts)) }
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_very_small)))

                IconButton(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_normal))
                        ),
                    onClick = {
                        searchCatFactsClick(searchQuery)
                    }
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(R.string.txt_search_facts),
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_very_small)))

            if (catFacts.itemCount > 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(dimensionResource(R.dimen.padding_normal)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_very_small))
                ) {
                    items(count = catFacts.itemCount) { index ->
                        val singleCatFact = catFacts[index]
                        if (singleCatFact != null) {
                            CatFactRow(catFact = singleCatFact, onFavoriteClick = { fact ->
                                toggleFavoriteStatusClick(fact)
                            }, isShowCatFactPopupVisible, selectedCatFact)
                        }
                    }
                    when (catFacts.loadState.append) {
                        is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.fillMaxWidth()) }
                        }

                        is LoadState.Error -> {
                            item { Text(stringResource(R.string.txt_error_loading_data)) }
                        }

                        else -> Unit
                    }
                }
            } else {
                Text(
                    text = errorMessage.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_normal)),
                    textAlign = TextAlign.Center
                )
            }
        }
        DeleteCatFactsPopup(
            isPopupVisible = isDeleteCatPopupVisible,
            onDismiss = { isDeleteCatPopupVisible = false },
            deleteAllCatFactsClick,
            { message -> viewModel.showToast(context, message) },
            catFacts.itemCount
        )
        ShowFactMiniPopup(
            isShowCatFactPopupVisible,
            onDismiss = { isShowCatFactPopupVisible.value = false },
            selectedCatFact.value
        )
    }
}

@Composable
fun CatFactRow(
    catFact: CatFactLocal,
    onFavoriteClick: (CatFactLocal) -> Unit,
    showCatFactPopupVisible: MutableState<Boolean>,
    catFactInPopup: MutableState<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_very_small))
            .clickable {
                catFactInPopup.value = catFact.fact
                showCatFactPopupVisible.value = true
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensionResource(R.dimen.padding_normal))
        ) {
            Text(
                text = catFact.fact,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis //if exceeds two lines, then show the triple dots
            )
            Text(text = "Length: ${catFact.length}", style = MaterialTheme.typography.bodySmall)
        }

        IconButton(
            onClick = { onFavoriteClick(catFact) },
            modifier = Modifier.size(dimensionResource(R.dimen.padding_xtra_large))
        ) {
            Icon(
                imageVector = if (catFact.isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = stringResource(R.string.txt_favourite_single_fact),
                tint = if (catFact.isFavourite) Color.Red else Color.Gray
            )
        }
    }
}

@Composable
fun ShowFactMiniPopup(
    isCatFactPopupVisible: MutableState<Boolean>,
    onDismiss: () -> Unit,
    catFact: String
) {
    if (isCatFactPopupVisible.value) {
        Popup(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.size_30))
                    .pointerInput(Unit) {
                        // If user taps outside of the popup, dismiss it
                        detectTapGestures(onTap = { onDismiss() })
                    }
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_normal))
                        )
                        .fillMaxWidth(0.9f)
                        .padding(
                            start = dimensionResource(R.dimen.padding_normal),
                            end = dimensionResource(R.dimen.padding_normal),
                            top = dimensionResource(R.dimen.padding_xtra_large),
                            bottom = dimensionResource(R.dimen.padding_xtra_large)
                        )
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_cat_sitting),
                        contentDescription = stringResource(R.string.txt_favourite_cat),
                        tint = Color.Black
                    )
                    Text(
                        modifier = Modifier.padding(top = dimensionResource(R.dimen.padding_small)),
                        text = catFact,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.background,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteCatFactsPopup(
    isPopupVisible: Boolean,
    onDismiss: () -> Unit,
    deleteAllCatFactsClick: () -> Unit,
    showToast: (String) -> Unit,
    catFacts: Int
) {
    val noMoreCatsTxt = stringResource(R.string.txt_no_more_facts)
    if (isPopupVisible) {
        Popup(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(R.dimen.size_30))
                    .pointerInput(Unit) {
                        // If user taps outside of the popup, dismiss it
                        detectTapGestures(onTap = { onDismiss() })
                    }
                    .testTag(stringResource(R.string.txt_delete_facts))
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_normal))
                        )
                        .fillMaxWidth(0.9f)
                        .padding(dimensionResource(R.dimen.padding_normal))
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.size_128))
                    ) {
                        AlertLottieAnimation()
                    }
                    Text(
                        text = stringResource(R.string.txt_delete_facts),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.background,
                        textAlign = TextAlign.Center
                    )


                    val onDeleteAllFactsClicked = {
                        if (catFacts > 0) {
                            deleteAllCatFactsClick()
                            onDismiss()
                        } else {
                            showToast(noMoreCatsTxt)
                        }
                    }
                    Button(
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.size_50),
                            end = dimensionResource(R.dimen.size_50),
                            top = dimensionResource(R.dimen.padding_large)
                        ),
                        onClick = onDeleteAllFactsClicked,
                    ) {
                        Text(text = stringResource(R.string.txt_delete_all_fact))
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertLottieAnimation() {
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.alert_popup))

    LottieAnimation(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewDeleteCatFactsPopup() {
    val isPopupVisible = true
    val catFacts = 5

    DeleteCatFactsPopup(
        isPopupVisible = isPopupVisible,
        onDismiss = { /* ignoring */ },
        deleteAllCatFactsClick = { /* ignoring */ },
        showToast = { /* ignoring */ },
        catFacts = catFacts
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewShowFactMiniPopup() {
    val isCatFactPopupVisible = remember { mutableStateOf(true) }
    val catFact = "Cats sleep for about 16 hours a day!"

    ShowFactMiniPopup(
        isCatFactPopupVisible = isCatFactPopupVisible,
        onDismiss = { isCatFactPopupVisible.value = false },
        catFact = catFact
    )
}

/*
Comment about why this interface exists is in FactScreen.kt line 408
 */
object PreviewKittyListAndSearchViewModel : KittyListAndSearchViewModelInt {
    override fun getAllStoredCatFacts() {}
    override fun getAllFavouriteCatFacts() {}
    override fun deleteAllCatFacts() {}
    override fun searchCatFacts(query: String) {}
    override fun toggleFavoriteStatus(catFact: CatFactLocal) {}
    override fun updateLoader(showLoader: Boolean) {}
    override fun showToast(context: Context, message: String) {}
    override val isLoading: StateFlow<Boolean> get() = MutableStateFlow(false)
    override val offlineMode: StateFlow<Boolean> get() = MutableStateFlow(false)
    override fun shareCatFact(context: Context, factToShare: String) {}
    override val catErrorMessage: StateFlow<String> get() = MutableStateFlow("")
    override fun isInternetAvailable(context: Context): Boolean { return true }
    override fun isClickable(): Boolean { return true }
    override var mCatFactsFromLocalDb: StateFlow<PagingData<CatFactLocal>> =
        MutableStateFlow(PagingData.empty())
}

@Preview(showBackground = true)
@Composable
fun PreviewKittyListAndSearch() {
    KittyListAndSearch(
        viewModel = PreviewKittyListAndSearchViewModel,
        onNavigateAction = {}
    )
}


