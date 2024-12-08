package jp.speakbuddy.edisonandroidexercise.data

import androidx.paging.PagingSource
import androidx.paging.PagingState


/**
 * I'm not entirely sure if this class is necessary, it returns an empty paging source but this may
 * not be required if I modified the try-catch for the DAO function usage call. Since my
 * implementation combines Favourite + Search + All Facts into one StateFlow for simplicity, this
 * is nice to have I think.
 */
class EmptyPagingSource<T : Any> : PagingSource<Int, T>() {
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        //I don't use a key, so can ignore this
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        //Default would be an empty PagingData
        return LoadResult.Page(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )
    }
}