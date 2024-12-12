package jp.speakbuddy.edisonandroidexercise.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal

@Dao
interface CatFactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addACatFact(catFact: CatFactLocal)

    @Query("SELECT * FROM cat_facts")
    fun getAllCatFactsAsList(): PagingSource<Int, CatFactLocal>

    @Query("SELECT * FROM cat_facts WHERE fact LIKE :kittyQuery")
    fun searchCatFacts(kittyQuery: String): PagingSource<Int, CatFactLocal>

    @Query("SELECT * FROM cat_facts ORDER BY id DESC LIMIT 1")
    suspend fun getLatestCatFact(): CatFactLocal?

    @Query("SELECT * FROM cat_facts ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomCatFact(): CatFactLocal?

    @Query("SELECT * FROM cat_facts WHERE factHashKey = :factHash LIMIT 1")
    suspend fun getCatFactByHash(factHash: String): CatFactLocal?

    @Query("DELETE FROM cat_facts")
    suspend fun deleteAllCatFacts()

    @Query("SELECT * FROM cat_facts WHERE isFavourite = 1")
    fun getFavouriteCatFacts(): PagingSource<Int, CatFactLocal>

    /**
    Technically you can update the CatFact's "favourite" field directly in the db in a single
    query, but scalability I think this is better long term if more variables are needed to be
    edited in a single fact at once so this can be reused to reduce some boilerplate
     **/
    @Update
    suspend fun updateCatFact(catFact: CatFactLocal)

}