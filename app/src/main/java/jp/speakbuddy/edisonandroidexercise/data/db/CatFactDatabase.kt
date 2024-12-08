package jp.speakbuddy.edisonandroidexercise.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import jp.speakbuddy.edisonandroidexercise.data.dao.CatFactDao
import jp.speakbuddy.edisonandroidexercise.data.entity.CatFactLocal

@Database(entities = [CatFactLocal::class], version = 1)
abstract class CatFactDatabase : RoomDatabase() {
    //Hilt handles the database builder, so singleton of the db is ensured.
    abstract fun catFactDao(): CatFactDao
}