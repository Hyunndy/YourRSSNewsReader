package com.example.myrealtripwithhyunndy.rsshelper

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Exception


class RSSFeedViewModel : ViewModel() {

    //최종
    private var rssList : MutableLiveData<MutableList<RSSItem>>? = MutableLiveData()

    //중간
    private var rssItems : List<RSSItem>? = null

    //끝
    private var tempItems : MutableList<RSSItem>? = mutableListOf()


    private val rssManager by lazy { RssListManager() }
    private var startPos : Int = 0
    private lateinit var job : Job

    init{
        loadRSSList()
    }

    fun getRSSList() : MutableLiveData<MutableList<RSSItem>>? = rssList

    fun getItemSize() : Int = tempItems!!.size


    private fun loadRSSList() {

        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val param = mapOf(
                    "hl" to "ko",
                    "gl" to "KR",
                    "ceid" to "KR:ko"
                )

                //@TODO
                rssItems = rssManager.getRssList(param)
                getDetailNews(0)
                Log.d("TEST33", "${rssItems?.size}@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")

            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

     fun getDetailNews(startIdx : Int){//rssItems : List<RSSItem>?) {

         if(rssItems == null) return

         if(startIdx > rssItems!!.size) return

          viewModelScope.launch(Dispatchers.Main) {
            try{
                if(job.isActive) job.join()
                if(rssItems == null) cancel()

                for(idx in startIdx..startIdx+5){//rssItems!!.indices) {

                    if(rssItems?.get(idx) == null) break

                    val temp : RSSItem = rssItems!![idx]

                    if(temp.link == null) {
                        temp.imgLink = ""
                        temp.description = ""
                    } else {
                        try{
                            val doc = getJsoup(temp.link!!)
                            temp.imgLink = doc.select("meta[property=og:image]")[0]?.attr("content")
                            temp.description =  doc.select("meta[property=og:description]")[0]?.attr("content")
                        } catch (e: Exception) {
                            temp.imgLink = ""
                            temp.description = ""
                        }
                    }

                    tempItems?.add(temp)
                }

                rssList?.value = tempItems

            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

     private suspend fun getJsoup(url : String) =

         withContext(Dispatchers.IO) {
             Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get()
         }

    /*
        // Jsoup 라이브러리를 이용해 이미지, 본문을 추출한다.
    private fun extractImageandDescFromLink(link : String){

        var newsDesc = ""
        var imgLink = ""


        if(link == "FailToLoadURL") {
            imgLink = "noImage"
            newsDesc = "......"
        } else {
                try {
                   var doc = Jsoup.connect(link).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get()
                   imgLink = doc.select("meta[property=og:image]")[0].attr("content")
                   newsDesc = doc.select("meta[property=og:description]")[0].attr("content")

                } catch ( e : Exception) {
                    imgLink = "noImage"
                    newsDesc = "......"

                    e.printStackTrace()
                }
        }

        rssNewsThumbnail.add(imgLink)
        rssNewsDesc.add(newsDesc)

        // 본문을 통해서 키워드 추출 api를 통해 얻은 JSON파일을 추출한다.
        keywordHelper.getJSONFromDesc(newsDesc)
    }
     */
}




