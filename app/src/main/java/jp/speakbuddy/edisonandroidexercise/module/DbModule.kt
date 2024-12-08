package jp.speakbuddy.edisonandroidexercise.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.speakbuddy.edisonandroidexercise.base.CommonConstants
import jp.speakbuddy.edisonandroidexercise.data.dao.CatFactDao
import jp.speakbuddy.edisonandroidexercise.data.db.CatFactDatabase

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): CatFactDatabase {
        return Room.databaseBuilder(
            context,
            CatFactDatabase::class.java,
            CommonConstants.CAT_FACT_LOCAL_DB_NAME
        ).build()
    }

    @Provides
    fun provideCatFactDao(database: CatFactDatabase): CatFactDao {
        return database.catFactDao()
    }

}