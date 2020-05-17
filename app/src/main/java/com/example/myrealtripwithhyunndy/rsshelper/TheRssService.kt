package com.example.myrealtripwithhyunndy.rsshelper

import android.util.Log
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

/*
Retrofit2 를 이용해서 HTTP를 처리한다.
Retrofit2은 HTTP의 REST API를 구현하기 위한 라이브러리. 외부 서버와 API를 통해 통신을 할 때 사용하는것.

REST란 Reperesentational State Transfer의 약자로 네트워크상 클라이언트의 통신방식을 말한다.
클라이언트의 응답에 대한 처리로 xml, json, text, rss 등을 지원한다.

https://howtodoinjava.com/retrofit2/retrofit-parse-rss-feed/
이 클래스에서는 Retrofit2 + simplexml컨버터를 통해 rss사이트에서 받아온 xml파일을     ..

https://www.w3schools.com/xml/note.xml
 */

const val BASE_URL = "https://news.google.com/"

// create service interface which will be invoked by retrofit to excute rss request. Notice the API URL is '/feed'
interface TheRssService {

    @GET("rss")
    fun getDeferredRssAsync(@QueryMap par : Map<String, String>) : Deferred<RSSFeedResponse>
}