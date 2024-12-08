package jp.speakbuddy.edisonandroidexercise.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_facts")
data class CatFactLocal(
    /**
     * I'm using a hash key as the unique ID to prevent duplicate facts being stored,
     * Technically you could use string compare(which is terrible), OR Index[] + unique = true
     * on the DB for 'fact' string value, OR the query can be WHERE fact = :fact as well.
     *
     * I chose hash key as it should be less cycles to process and quicker in the long term.
     * I checked the cat fact website, and it seems to have hundreds of facts...
     * Since I don't know what the maximum length a cat fact could be OR if the db were to get too
     * large with facts, a hash key would be a quicker query since it's a fixed SHA256 length.
     * I know it may not make a real world difference here, but scalability should be accounted for.
     * **/
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val factHashKey: String, // doesn't need to be a primary key since I have a custom dupe check
    val fact: String,
    val length: Int,
    val isFavourite: Boolean
)