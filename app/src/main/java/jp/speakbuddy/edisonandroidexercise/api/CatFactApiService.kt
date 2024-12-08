package jp.speakbuddy.edisonandroidexercise.api

import jp.speakbuddy.edisonandroidexercise.model.Fact
import retrofit2.http.GET

interface CatFactApiService {

    /**
    Typically I prefer adding versioning for API endpoints, eg: "v1/facts" for backwards
    compatibility for when endpoints add more data but older app versions don't handle it.
    Behaviours should be consistent for UX.
     **/
    @GET("fact")
    suspend fun getCatFact(): Fact

}