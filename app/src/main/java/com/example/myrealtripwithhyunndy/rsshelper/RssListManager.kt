package com.example.myrealtripwithhyunndy.rsshelper

import android.util.Log

/*

Retrofit API에게 rss리스트를 받아와서 실제 데이터로 변환시켜 최종적으로 RSS목록을 반환해주는 매니저 클래스.

 */


class RssListManager(private val api: RestApi = RestApi()) {

    //Retrofit api로부터 RSSFeed를 요청해서 받아온다.
    suspend fun getRssList(par : Map<String, String>) : List<RSSItem>? {

        val feed = api.getRssListRetrofit(par)
        return process(feed)
    }

    // Retrofit으로 부터 받아온 데이터를 실제 프로젝트에서 쓰이는 Data Class로 변환.
    private fun process(response : RSSFeedResponse?) : List<RSSItem>? {
        val list = response?.channel?.items?.map {
            RSSItem(
                it.title,
                it.link,
                it.description
            )
        }

        return list
    }

}