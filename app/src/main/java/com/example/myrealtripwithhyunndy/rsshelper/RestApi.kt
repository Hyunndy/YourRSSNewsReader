package com.example.myrealtripwithhyunndy.rsshelper

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

@Suppress("DEPRECATION")
class RestApi {

    private val theRssService : TheRssService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create()) // simpleXml 사용 -> deprecated 대체제 필요.
            .addCallAdapterFactory(CoroutineCallAdapterFactory()) // 코루틴 사용
            .build()

        theRssService = retrofit.create(TheRssService::class.java)
    }

    suspend fun getRssListRetrofit(param : Map<String, String>) : RSSFeedResponse? {
        return theRssService.getDeferredRssAsync(param).await()
    }
}