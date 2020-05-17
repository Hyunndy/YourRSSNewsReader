package com.example.myrealtripwithhyunndy.rsshelper

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.Exception


class RSSFeedViewModel : ViewModel() {

    private var rssList : MutableLiveData<List<RSSItem>>? = MutableLiveData()
    private val rssManager by lazy { RssListManager() }
    private var startPos : Int = 0
    private lateinit var job : Job

    init{
        loadRSSList()
        getDetailNews(0)
    }

    fun getRSSList() : MutableLiveData<List<RSSItem>>? = rssList

    private fun loadRSSList() {

        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val param = mapOf(
                    "hl" to "ko",
                    "gl" to "KR",
                    "ceid" to "KR:ko"
                )

                val retrivedRSSList = rssManager.getRssList(param)
                rssList?.value = retrivedRSSList


            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getDetailNews(startPos : Int) {

          viewModelScope.launch(Dispatchers.Main) {
            try{
                if(job.isActive) job.join()

                for(idx in startPos..startPos+9) {

                    // 취소
                    if(idx >= rssList?.value!!.size) cancel()

                    val temp : RSSItem = rssList?.value!![idx]

                    if(temp.link == "") {
                        rssList?.value?.get(idx)?.imgLink = ""

                    } else {

                    }


                }




            } catch (e : Exception) {
                e.printStackTrace()
            }


        }

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




