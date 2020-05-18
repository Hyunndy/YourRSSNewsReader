package com.example.myrealtripwithhyunndy.rsshelper

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.lang.Exception


class RSSFeedViewModel : ViewModel() {

    //최종적으로 UI에 뿌려지게될 데이터
    private var rssList : MutableLiveData<MutableList<RSSItem>>? = MutableLiveData()

    //API로부터 받아온 리스트
    private var apiList : List<RSSItem>? = null

    //API로부터 받아온 리스트 + Jsoup으로 만든 리스트
    private var jsoupList : MutableList<RSSItem> = mutableListOf()

    var jsoupNum : Int = 0

    private val rssManager by lazy { RssListManager() }
    private lateinit var job : Job
    private lateinit var  job2 : Job


    fun getRSSList() : MutableLiveData<MutableList<RSSItem>>? = rssList

    fun getItemSize() : Int = jsoupList.size

    fun refresh() {
        apiList = null
        jsoupList.clear()
        jsoupNum = 0
    }


    fun loadRSSList() {

        job = viewModelScope.launch(Dispatchers.Main) {
            try {
                val param = mapOf(
                    "hl" to "ko",
                    "gl" to "KR",
                    "ceid" to "KR:ko"
                )

                apiList = rssManager.getRssList(param)
                getDetailNews()


            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

     fun getDetailNews() {

         //api로부터 받아온게 없다면 return
         //받아오려는 데이터가 apiList보다 커졌다면
         if(apiList == null|| apiList!!.size <= jsoupNum) return


          job2 = viewModelScope.launch(Dispatchers.Main) {
            try{
                val startIdx = jsoupNum

                //6개씩
                for(idx in startIdx..startIdx+5) {

                    Log.d("TEST33" , "JSOUP 요청 번호 = $idx")

                    if(idx >= apiList!!.size) break

                    if(apiList?.get(idx) == null) break

                    val temp : RSSItem = apiList?.get(idx)!!

                    // 링크가 없는 경우
                    if(temp.link == "") {
                        temp.imgLink = ""
                        temp.description = ""
                    } else {
                        // JSOUP으로 HTML 태그 긁어오기
                        try{
                            val doc = getJsoup(temp.link!!)
                            if(doc != null) {
                                temp.imgLink = doc.select("meta[property=og:image]")[0]?.attr("content")
                                temp.description =  doc.select("meta[property=og:description]")[0]?.attr("content")
                            } else {
                                temp.imgLink = ""
                                temp.description = ""
                            }
                        } catch (e: Exception) {
                            temp.imgLink = ""
                            temp.description = ""
                        }
                    }

                    jsoupList.add(temp)
                    jsoupNum += 1
                }

                rssList?.value = jsoupList

            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }

     private suspend fun getJsoup(url : String)  =
         withContext(Dispatchers.IO) {
             try{
                 Jsoup.connect(url)
                     .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                     .referrer("http://www.google.com")
                     .get()
             } catch (e: Exception) {
                null
             }
         }
}




