package jp.speakbuddy.edisonandroidexercise.module

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jp.speakbuddy.edisonandroidexercise.api.CatFactApiService
import jp.speakbuddy.edisonandroidexercise.base.CommonConstants
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val jsonCustom = Json { //custom json to reduce errors in parsing
            ignoreUnknownKeys = true
            isLenient = true
        }
        val contentType = "application/json".toMediaType()
        /*
        Response headers from api doesn't specifically say UTF-8 in content-type,
        It loads okay, so I hope it's okay not to declare as it's a very common response CharSet
        */

        return Retrofit.Builder()
            .baseUrl(CommonConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(jsonCustom.asConverterFactory(contentType))
            .build()
    }

    /**
     * We should specify timeouts, here I will set it as 30 seconds, if there are timeouts
     * it will be handled by ErrorHandler automatically.
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
        }.build()
    }

    @Provides
    @Singleton
    fun provideCatFactApi(retrofit: Retrofit): CatFactApiService {
        return retrofit.create(CatFactApiService::class.java)
    }
}