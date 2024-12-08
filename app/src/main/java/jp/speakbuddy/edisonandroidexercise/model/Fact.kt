package jp.speakbuddy.edisonandroidexercise.model

import kotlinx.serialization.Serializable


@Serializable
data class Fact(
    /**
    Usually I would call this xxxResponse so it's obvious it's an API response, but since the
    response is a single fact, I opted not to do this and kept it simple by using the Fact data
    class.

    Usually I would also make each item inside as nullable as app shouldn't always trust backend
    will return the expected values as backend changes, in this case I won't.
    APIs ideally should also have versioning like v1 / v2 mainly for backwards compatibility with
    older versions of the CatFact App that are not using the latest deployment on backend
     **/
    val fact: String,
    val length: Int,
    val isFavourite: Boolean = false
)